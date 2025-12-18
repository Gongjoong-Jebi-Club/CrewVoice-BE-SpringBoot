package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.domain.*;
import org.skala.crewvoice.dto.request.CallbackRequest;
import org.skala.crewvoice.dto.request.EvaluationCreateRequest;
import org.skala.crewvoice.dto.request.FastAPIEvaluationRequest;
import org.skala.crewvoice.dto.response.*;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 평가(시험) 비즈니스 로직 처리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final FinalGradeRepository finalGradeRepository;
    private final EvaluationTargetUserRepository evaluationTargetUserRepository;
    private final SubmissionStep2Repository submissionStep2Repository;
    private final EvaluationStep2Repository evaluationStep2Repository;
    private final SubmissionStep3Repository submissionStep3Repository;
    private final EvaluationStep3Repository evaluationStep3Repository;
    private final S3Service s3Service;
    private final WebClient fastapiWebClient;
    private final EvaluationCallbackService evaluationCallbackService;
    private final EvaluationTargetService evaluationTargetService;

    @Value("${fastapi.callback-url}")
    private String fastapiCallbackUrl;


    /**
     * 평가(시험) 생성
     *
     * @param request 평가 생성 요청 데이터 (이미 @Valid로 검증됨)
     * @return 생성된 평가 ID
     *
     * NOTE: DTO 검증은 컨트롤러에서 @Valid로 자동 수행됨
     * - @ValidWeightSum: 가중치 합계 = 100
     * - @ValidDateRange: startDate < endDate, startDate >= now
     */
    @Transactional
    public EvaluationCreateResponse createEvaluation(EvaluationCreateRequest request) {
        log.info("평가 생성 시작 - 평가명: {}", request.getName());

        // 1. Entity 생성
        Evaluation evaluation = Evaluation.builder()
                .evaluationName(request.getName())
                .evaluationType(request.getEvaluationType() != null ? request.getEvaluationType() : EvaluationType.EXAM)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .weightPronunciation(BigDecimal.valueOf(request.getWeightPronunciation()))
                .weightIntonation(BigDecimal.valueOf(request.getWeightIntonation()))
                .weightAccent(BigDecimal.valueOf(request.getWeightAccent()))
                .weightPause(BigDecimal.valueOf(request.getWeightPause()))
                .weightTone(BigDecimal.valueOf(request.getWeightTone()))
                .weightSpeed(BigDecimal.valueOf(request.getWeightSpeed()))
                // FastAPI 파라미터
                .weightClarity(request.getWeightClarity())
                .weightStability(request.getWeightStability())
                .weightBrightness(request.getWeightBrightness())
                .weightPitchPosition(request.getWeightPitchPosition())
                .weightFluency(request.getWeightFluency())
                .weightPosition(request.getWeightPosition())
                .weightLength(request.getWeightLength())
                .weightPitch(request.getWeightPitch())
                .weightEnergy(request.getWeightEnergy())
                .weightBaseline(request.getWeightBaseline())
                .weightIso(request.getWeightIso())
                .deviationThreshold(request.getDeviationThreshold())
                .build();

        // 2. Entity 레벨 검증 (불변 조건)
        evaluation.validateWeights();
        evaluation.validateDateRange();
        evaluation.validateAllFastAPIWeights();

        // 3. 저장
        Evaluation savedEvaluation = evaluationRepository.save(evaluation);
        log.info("평가 생성 완료 - 평가ID: {}", savedEvaluation.getEvaluationId());

        // 4. 평가 대상자 자동 등록 (EvaluationTargetService로 위임)
        evaluationTargetService.registerTargetUsers(savedEvaluation);

        // 5. 응답 DTO 반환
        return EvaluationCreateResponse.from(savedEvaluation.getEvaluationId());
    }

    /**
     * 전체 평가 목록 조회 (관리자용)
     * 평가 기간과 상관없이 모든 평가를 조회합니다.
     *
     * @return 평가 목록 (targetCount, completedCount 포함)
     */
    public EvaluationListResponse getAllEvaluations() {
        log.info("전체 평가 목록 조회 시작 (관리자용)");

        // 1. 모든 평가 조회 (최신순) - 날짜 조건 없음
        List<Evaluation> evaluations = evaluationRepository.findAllByOrderByCreatedAtDesc();

        // 2. 각 평가별로 대상자 수와 완료자 수를 조회하고 Response DTO 생성
        List<EvaluationResponse> responseList = evaluations.stream()
                .map(evaluation -> {
                    // 해당 평가의 대상자 수 (EvaluationTargetService로 위임)
                    Integer targetCount = evaluationTargetService.getTargetCount(
                            evaluation.getEvaluationId()
                    );

                    // 해당 평가의 완료자 수 (final_grades 테이블에서 조회)
                    Integer completedCount = finalGradeRepository.countByEvaluationId(
                            evaluation.getEvaluationId()
                    );

                    return EvaluationResponse.of(evaluation, targetCount, completedCount);
                })
                .toList();

        log.info("전체 평가 목록 조회 완료 - 총 {}개", responseList.size());

        return EvaluationListResponse.of(responseList);
    }

    /**
     * 현재 진행 중인 평가 목록 조회 (피평가자용)
     * 현재 시간이 평가 기간 내(startDate ~ endDate)에 있는 평가만 조회합니다.
     * CREW 사용자의 경우 본인에게 배정된 평가만 조회됩니다 (evaluation.created_at > user.created_at)
     *
     * @param user 인증된 사용자
     * @return 진행 중인 평가 목록 (사용자별 응시 여부 포함)
     */
    public ActiveEvaluationListResponse getActiveEvaluations(User user) {
        log.info("현재 진행 중인 평가 목록 조회 시작 (피평가자용) - userId: {}, role: {}",
                user.getId(), user.getRole());

        // 1. 현재 시간 기준으로 진행 중인 평가만 조회 (startDate <= now <= endDate)
        // CREW인 경우 user.created_at 이후 생성된 평가만, ADMIN/MANAGER는 모든 평가 조회
        OffsetDateTime now = OffsetDateTime.now();
        List<Evaluation> activeEvaluations;

        if (user.isCrew()) {
            // CREW 사용자: 사용자 생성 이후 생성된 평가만 조회 (배정된 평가만)
            activeEvaluations = evaluationRepository.findActiveEvaluationsForCrew(now, user.getCreatedAt());
            log.info("진행 중인 평가 수 (CREW 필터링 적용): {}", activeEvaluations.size());
        } else {
            // ADMIN/MANAGER: 모든 활성 평가 조회
            activeEvaluations = evaluationRepository.findActiveEvaluations(now);
            log.info("진행 중인 평가 수 (모든 평가): {}", activeEvaluations.size());
        }

        // 2. Response DTO 변환 (사용자별 is_taken 포함)
        List<ActiveEvaluationResponse> responseList = activeEvaluations.stream()
                .map(evaluation -> {
                    // 해당 사용자의 평가 응시 여부 조회
                    Boolean isTaken = evaluationTargetUserRepository
                            .findIsTakenByUserIdAndEvaluationId(user.getId(), evaluation.getEvaluationId())
                            .orElse(false);  // 배정되지 않은 평가는 false

                    return ActiveEvaluationResponse.from(evaluation, isTaken);
                })
                .toList();

        log.info("현재 진행 중인 평가 목록 조회 완료");

        return ActiveEvaluationListResponse.of(responseList);
    }

    /**
     * 평가 시작 - 비동기 처리 (3번 API)
     * 제출된 음성 파일에 대한 평가를 FastAPI로 요청합니다.
     * Fire-and-Forget 방식으로 즉시 반환하고 백그라운드에서 평가를 진행합니다.
     *
     * @param submissionId 제출 ID
     * @param step 평가 단계 (2 또는 3)
     *
     * NOTE: @Async와 @Transactional을 함께 사용하면 안 됨
     * - 트랜잭션이 커밋되기 전에 비동기 작업이 시작될 수 있음
     * - S3, WebClient 등 외부 API 호출은 트랜잭션 외부에서 실행해야 함
     */
    @Async("evaluationTaskExecutor")
    public void startEvaluationAsync(Long submissionId, Integer step) {
        log.info("평가 시작 (비동기) - submissionId: {}, step: {}", submissionId, step);

        try {
            if (step == 2) {
                processStep2Evaluation(submissionId);
            } else if (step == 3) {
                processStep3Evaluation(submissionId);
            } else {
                log.error("❌ [평가 시작 실패] 지원하지 않는 step - submissionId: {}, step: {}", submissionId, step);
            }
        } catch (EntityNotFoundException e) {
            log.error("❌ [평가 시작 실패] 제출 내역을 찾을 수 없음 - submissionId: {}, step: {}, error: {}",
                    submissionId, step, e.getMessage());
        } catch (Exception e) {
            log.error("❌ [평가 시작 실패] 예상치 못한 오류 발생 - submissionId: {}, step: {}, errorType: {}, error: {}",
                    submissionId, step, e.getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    /**
     * Step2 평가 처리
     * 한 언어당 하나의 오디오 파일을 FastAPI로 전송
     *
     * NOTE: 트랜잭션 경계 최소화
     * - DB 작업만 @Transactional 메서드로 분리
     * - S3, WebClient 호출은 트랜잭션 외부에서 실행
     * - submission을 한 번만 조회하여 Race Condition 방지
     */
    private void processStep2Evaluation(Long submissionId) {
        log.info("Step2 평가 처리 시작 - submissionId: {}", submissionId);

        // STEP 1: Submission 조회 (JOIN FETCH로 필요한 데이터 모두 로딩)
        SubmissionStep2 submission = submissionStep2Repository
                .findByIdWithQuestion(submissionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "제출 내역을 찾을 수 없습니다. submissionId: " + submissionId,
                        org.skala.crewvoice.exception.ErrorCode.SUBMISSION_NOT_FOUND
                ));

        // STEP 2: 평가 레코드 생성 (submission 객체 전달)
        EvaluationStep2 evaluation = createEvaluationStep2Record(submission);
        String taskId = evaluation.getTaskId();

        // STEP 3: Submission 정보 추출 (메모리에서 재사용)
        QuestionStep2 question = submission.getQuestion();
        Language language = submission.getLanguage();
        Evaluation evaluationEntity = submission.getEvaluation();

        // STEP 4: S3 Pre-signed URL 생성 (트랜잭션 외부 - 외부 API 호출)
        String presignedUrl = s3Service.generatePresignedUrl(
                submission.getAudioFileUrl(),
                (int) Duration.ofMinutes(15).toSeconds()
        );
        log.info("S3 Pre-signed URL 생성 완료");

        // STEP 5: 스크립트 선택
        String script = (language == Language.KOREAN)
                ? question.getScriptKorean()
                : question.getScriptEnglish();

        log.info("평가 스크립트 선택 완료 - language: {}, scriptLength: {}",
                language.getCode(), script.length());

        // STEP 6: FastAPI 요청 (트랜잭션 외부 - 외부 API 호출)
        sendEvaluationRequest(taskId, presignedUrl, script, language.getCode(), evaluationEntity, evaluation);

        log.info("Step2 평가 시작 완료 (비동기) - submissionId: {}, taskId: {}", submissionId, taskId);
    }

    /**
     * Step2 평가 레코드 생성 (트랜잭션 필요)
     *
     * @param submission 이미 조회된 submission 객체 (재조회하지 않음)
     */
    @Transactional
    protected EvaluationStep2 createEvaluationStep2Record(SubmissionStep2 submission) {
        Language language = submission.getLanguage();
        log.info("제출 내역 조회 완료 - submissionId: {}, language: {}",
                submission.getSubmissionId(), language.getCode());

        // Task ID 생성
        String taskId = UUID.randomUUID().toString();
        log.info("Task ID 생성 완료 - taskId: {}", taskId);

        // EvaluationStep2 레코드 생성 및 저장
        EvaluationStep2 evaluation = EvaluationStep2.builder()
                .submission(submission)
                .taskId(taskId)
                .language(language)
                .status("PROCESSING")
                .createdAt(LocalDateTime.now())
                .build();

        EvaluationStep2 saved = evaluationStep2Repository.save(evaluation);
        log.info("평가 레코드 생성 완료 - evaluationStep2Id: {}, taskId: {}", saved.getId(), taskId);

        return saved;
    }

    /**
     * FastAPI 평가 요청 전송 (트랜잭션 외부)
     */
    private void sendEvaluationRequest(String taskId, String audioUrl, String script,
                                       String languageCode, Evaluation evaluationEntity, EvaluationStep2 evaluation) {
        sendFastAPIRequest(taskId, audioUrl, script, languageCode, evaluationEntity,
                () -> markEvaluationStep2AsFailed(evaluation.getId(), "FastAPI 요청 실패"));
    }

    /**
     * Evaluation 엔티티로부터 FastAPI 가중치 DTO 생성
     */
    private FastAPIEvaluationRequest.WeightsDTO buildFastAPIWeights(Evaluation evaluation) {
        return FastAPIEvaluationRequest.WeightsDTO.builder()
                .weightClarity(evaluation.getWeightClarity().doubleValue())
                .weightStability(evaluation.getWeightStability().doubleValue())
                .weightBrightness(evaluation.getWeightBrightness().doubleValue())
                .weightPitchPosition(evaluation.getWeightPitchPosition().doubleValue())
                .weightFluency(evaluation.getWeightFluency().doubleValue())
                .weightPosition(evaluation.getWeightPosition().doubleValue())
                .weightLength(evaluation.getWeightLength().doubleValue())
                .weightPitch(evaluation.getWeightPitch().doubleValue())
                .weightEnergy(evaluation.getWeightEnergy().doubleValue())
                .weightBaseline(evaluation.getWeightBaseline().doubleValue())
                .weightIso(evaluation.getWeightIso().doubleValue())
                .deviationThreshold(evaluation.getDeviationThreshold().doubleValue())
                .gender("F")
                .build();
    }

    /**
     * 공통 FastAPI 요청 전송 로직
     * Step2와 Step3에서 공통으로 사용
     */
    private void sendFastAPIRequest(String taskId, String audioUrl, String script,
                                     String languageCode, Evaluation evaluation, Runnable onFailure) {
        // FastAPI 가중치 DTO 생성
        FastAPIEvaluationRequest.WeightsDTO weights = buildFastAPIWeights(evaluation);

        FastAPIEvaluationRequest fastapiRequest = FastAPIEvaluationRequest.builder()
                .taskId(taskId)
                .audioUrl(audioUrl)
                .script(script)
                .language(languageCode)
                .callbackUrl(fastapiCallbackUrl)
                .weights(weights)
                .build();

        log.info("FastAPI 요청 DTO 생성 완료 - taskId: {}, callbackUrl: {}, gender: F", taskId, fastapiCallbackUrl);

        // WebClient로 FastAPI 비동기 호출 (Fire-and-Forget)
        fastapiWebClient.post()
                .uri("/api/evaluate")
                .bodyValue(fastapiRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe(
                        result -> log.info("FastAPI 평가 요청 성공 - taskId: {}", taskId),
                        error -> {
                            log.error("FastAPI 평가 요청 실패 - taskId: {}, error: {}",
                                    taskId, error.getMessage());
                            onFailure.run();
                        }
                );
    }

    /**
     * Step2 평가 실패 처리 (별도 트랜잭션)
     */
    @Transactional
    protected void markEvaluationStep2AsFailed(Long evaluationId, String errorMessage) {
        EvaluationStep2 evaluation = evaluationStep2Repository.findById(evaluationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "평가 레코드를 찾을 수 없습니다.",
                        org.skala.crewvoice.exception.ErrorCode.EVALUATION_NOT_FOUND
                ));
        evaluation.markAsFailed(errorMessage);
        evaluationStep2Repository.save(evaluation);
    }

    /**
     * Step3 평가 처리
     * Step2와 동일하게 한 언어당 하나의 오디오 파일을 FastAPI로 전송
     *
     * NOTE: 트랜잭션 경계 최소화
     * - DB 작업만 @Transactional 메서드로 분리
     * - S3, WebClient 호출은 트랜잭션 외부에서 실행
     * - submission을 한 번만 조회하여 Race Condition 방지
     */
    private void processStep3Evaluation(Long submissionId) {
        log.info("Step3 평가 처리 시작 - submissionId: {}", submissionId);

        // STEP 1: Submission 조회 (JOIN FETCH로 필요한 데이터 모두 로딩)
        SubmissionStep3 submission = submissionStep3Repository
                .findByIdWithQuestion(submissionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Step3 제출 내역을 찾을 수 없습니다. submissionId: " + submissionId,
                        org.skala.crewvoice.exception.ErrorCode.SUBMISSION_NOT_FOUND
                ));

        // STEP 2: 평가 레코드 생성 (submission 객체 전달)
        EvaluationStep3 evaluation = createEvaluationStep3Record(submission);
        String taskId = evaluation.getTaskId();

        // STEP 3: Submission 정보 추출 (메모리에서 재사용)
        QuestionStep3 question = submission.getQuestion();
        Language language = submission.getLanguage();
        Evaluation evaluationEntity = submission.getEvaluation();

        // STEP 4: S3 Pre-signed URL 생성 (트랜잭션 외부 - 외부 API 호출)
        String presignedUrl = s3Service.generatePresignedUrl(
                submission.getAudioFileUrl(),
                (int) Duration.ofMinutes(15).toSeconds()
        );
        log.info("S3 Pre-signed URL 생성 완료");

        // STEP 5: 스크립트 선택
        String script = (language == Language.KOREAN)
                ? question.getScriptKorean()
                : question.getScriptEnglish();

        log.info("평가 스크립트 선택 완료 - language: {}, scriptLength: {}",
                language.getCode(), script.length());

        // STEP 6: FastAPI 요청 (트랜잭션 외부 - 외부 API 호출)
        sendEvaluationRequestForStep3(taskId, presignedUrl, script, language.getCode(), evaluationEntity, evaluation);

        log.info("Step3 평가 시작 완료 (비동기) - submissionId: {}, taskId: {}", submissionId, taskId);
    }

    /**
     * Step3 평가 레코드 생성 (트랜잭션 필요)
     *
     * @param submission 이미 조회된 submission 객체 (재조회하지 않음)
     */
    @Transactional
    protected EvaluationStep3 createEvaluationStep3Record(SubmissionStep3 submission) {
        Language language = submission.getLanguage();
        log.info("Step3 제출 내역 조회 완료 - submissionId: {}, language: {}, partOrder: {}",
                submission.getSubmissionId(), language.getCode(), submission.getPartOrder());

        // Task ID 생성
        String taskId = UUID.randomUUID().toString();
        log.info("Task ID 생성 완료 - taskId: {}", taskId);

        // EvaluationStep3 레코드 생성 및 저장
        EvaluationStep3 evaluation = EvaluationStep3.builder()
                .submission(submission)
                .taskId(taskId)
                .language(language)
                .status("PROCESSING")
                .createdAt(LocalDateTime.now())
                .build();

        EvaluationStep3 saved = evaluationStep3Repository.save(evaluation);
        log.info("Step3 평가 레코드 생성 완료 - evaluationStep3Id: {}, taskId: {}", saved.getId(), taskId);

        return saved;
    }

    /**
     * FastAPI 평가 요청 전송 - Step3용 (트랜잭션 외부)
     */
    private void sendEvaluationRequestForStep3(String taskId, String audioUrl, String script,
                                               String languageCode, Evaluation evaluationEntity, EvaluationStep3 evaluation) {
        sendFastAPIRequest(taskId, audioUrl, script, languageCode, evaluationEntity,
                () -> markEvaluationStep3AsFailed(evaluation.getId(), "FastAPI 요청 실패"));
    }

    /**
     * Step3 평가 실패 처리 (별도 트랜잭션)
     */
    @Transactional
    protected void markEvaluationStep3AsFailed(Long evaluationId, String errorMessage) {
        EvaluationStep3 evaluation = evaluationStep3Repository.findById(evaluationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "평가 레코드를 찾을 수 없습니다.",
                        org.skala.crewvoice.exception.ErrorCode.EVALUATION_NOT_FOUND
                ));
        evaluation.markAsFailed(errorMessage);
        evaluationStep3Repository.save(evaluation);
    }

    /**
     * FastAPI 평가 완료 콜백 처리 (5번 API)
     * EvaluationCallbackService로 위임
     *
     * @param request FastAPI 콜백 요청 데이터
     */
    @Transactional
    public void handleCallback(CallbackRequest request) {
        evaluationCallbackService.handleCallback(request);
    }
}
