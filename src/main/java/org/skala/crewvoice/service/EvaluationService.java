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
@Transactional(readOnly = true)
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final FinalGradeRepository finalGradeRepository;
    private final UserRepository userRepository;
    private final EvaluationTargetUserRepository evaluationTargetUserRepository;
    private final SubmissionStep2Repository submissionStep2Repository;
    private final EvaluationStep2Repository evaluationStep2Repository;
    private final SubmissionStep3Repository submissionStep3Repository;
    private final EvaluationStep3Repository evaluationStep3Repository;
    private final S3Service s3Service;
    private final WebClient fastapiWebClient;

    @Value("${fastapi.callback-url}")
    private String fastapiCallbackUrl;


    /**
     * 평가(시험) 생성
     *
     * @param request 평가 생성 요청 데이터
     * @return 생성된 평가 ID
     */
    @Transactional
    public EvaluationCreateResponse createEvaluation(EvaluationCreateRequest request) {
        log.info("평가 생성 시작 - 평가명: {}", request.getName());

        // 1. Request DTO 레벨 검증
        request.validateDateRange();
        request.validateWeightSum();

        // 2. Entity 생성
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
                .build();

        // 3. Entity 레벨 검증
        evaluation.validateWeights();
        evaluation.validateDateRange();

        // 4. 저장
        Evaluation savedEvaluation = evaluationRepository.save(evaluation);

        log.info("평가 생성 완료 - 평가ID: {}", savedEvaluation.getEvaluationId());

        // 5. 현재 시점의 모든 ACTIVE CREW 사용자 조회 및 평가 대상자 자동 등록
        List<User> crewUsers = userRepository.findAllActiveCrewMembers();
        log.info("조회된 CREW 사용자 수: {}", crewUsers.size());

        if (!crewUsers.isEmpty()) {
            List<EvaluationTargetUser> targetUsers = crewUsers.stream()
                    .map(user -> {
                        log.debug("평가 대상자 추가: userId={}, userName={}", user.getId(), user.getName());
                        return EvaluationTargetUser.builder()
                                .evaluation(savedEvaluation)
                                .user(user)
                                .build();
                    })
                    .collect(Collectors.toList());

            List<EvaluationTargetUser> savedTargetUsers = evaluationTargetUserRepository.saveAll(targetUsers);
            log.info("평가 대상자 자동 등록 완료 - 대상자 수: {}", savedTargetUsers.size());
        } else {
            log.warn("등록할 CREW 사용자가 없습니다. 평가 대상자가 등록되지 않았습니다.");
        }

        // 6. 응답 DTO 반환
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
                    // 해당 평가의 대상자 수 (evaluation_target_users 테이블에서 조회)
                    Integer targetCount = evaluationTargetUserRepository
                            .findByEvaluationEvaluationId(evaluation.getEvaluationId())
                            .size();

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
     *
     * @return 진행 중인 평가 목록
     */
    public ActiveEvaluationListResponse getActiveEvaluations() {
        log.info("현재 진행 중인 평가 목록 조회 시작 (피평가자용)");

        // 1. 현재 시간 기준으로 진행 중인 평가만 조회 (startDate <= now <= endDate)
        OffsetDateTime now = OffsetDateTime.now();
        List<Evaluation> activeEvaluations = evaluationRepository.findActiveEvaluations(now);

        log.info("진행 중인 평가 수: {}", activeEvaluations.size());

        // 2. Response DTO 변환
        List<ActiveEvaluationResponse> responseList = activeEvaluations.stream()
                .map(ActiveEvaluationResponse::from)
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
     */
    @Async("evaluationTaskExecutor")
    @Transactional
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
     */
    private void processStep2Evaluation(Long submissionId) {
        log.info("Step2 평가 처리 시작 - submissionId: {}", submissionId);

        // STEP 1: Submission 조회 (JOIN FETCH로 N+1 방지)
        SubmissionStep2 submission = submissionStep2Repository
                .findByIdWithQuestion(submissionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "제출 내역을 찾을 수 없습니다. submissionId: " + submissionId,
                        org.skala.crewvoice.exception.ErrorCode.SUBMISSION_NOT_FOUND
                ));

        QuestionStep2 question = submission.getQuestion();
        Language language = submission.getLanguage();

        log.info("제출 내역 조회 완료 - submissionId: {}, language: {}, questionId: {}",
                submissionId, language.getCode(), question.getQuestionId());

        // STEP 2: Task ID 생성 (UUID)
        String taskId = UUID.randomUUID().toString();
        log.info("Task ID 생성 완료 - taskId: {}", taskId);

        // STEP 3: EvaluationStep2 레코드 생성 (status='PROCESSING')
        EvaluationStep2 evaluation = EvaluationStep2.builder()
                .submission(submission)
                .taskId(taskId)
                .language(language)
                .status("PROCESSING")
                .createdAt(LocalDateTime.now())
                .build();

        evaluationStep2Repository.save(evaluation);
        log.info("평가 레코드 생성 완료 - evaluationStep2Id: {}, taskId: {}", evaluation.getId(), taskId);

        // STEP 4: S3 Pre-signed URL 생성 (15분 유효)
        String presignedUrl = s3Service.generatePresignedUrl(
                submission.getAudioFileUrl(),
                (int) Duration.ofMinutes(15).toSeconds()
        );
        log.info("S3 Pre-signed URL 생성 완료");

        // STEP 5: 스크립트 선택 (언어에 따라)
        String script = (language == Language.KOREAN)
                ? question.getScriptKorean()
                : question.getScriptEnglish();

        log.info("평가 스크립트 선택 완료 - language: {}, scriptLength: {}",
                language.getCode(), script.length());

        // STEP 6: FastAPI 요청 DTO 생성
        FastAPIEvaluationRequest fastapiRequest = FastAPIEvaluationRequest.builder()
                .taskId(taskId)
                .audioUrl(presignedUrl)
                .script(script)
                .language(language.getCode())
                .callbackUrl(fastapiCallbackUrl)
                .build();

        log.info("FastAPI 요청 DTO 생성 완료 - taskId: {}, callbackUrl: {}", taskId, fastapiCallbackUrl);

        // STEP 7: WebClient로 FastAPI 비동기 호출 (Fire-and-Forget)
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
                            // 평가 상태를 FAILED로 변경
                            evaluation.markAsFailed("FastAPI 요청 실패: " + error.getMessage());
                            evaluationStep2Repository.save(evaluation);
                        }
                );

        log.info("Step2 평가 시작 완료 (비동기) - submissionId: {}, taskId: {}", submissionId, taskId);
    }

    /**
     * Step3 평가 처리
     * Step2와 동일하게 한 언어당 하나의 오디오 파일을 FastAPI로 전송
     */
    private void processStep3Evaluation(Long submissionId) {
        log.info("Step3 평가 처리 시작 - submissionId: {}", submissionId);

        // STEP 1: Submission 조회 (JOIN FETCH로 N+1 방지)
        SubmissionStep3 submission = submissionStep3Repository
                .findByIdWithQuestion(submissionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Step3 제출 내역을 찾을 수 없습니다. submissionId: " + submissionId,
                        org.skala.crewvoice.exception.ErrorCode.SUBMISSION_NOT_FOUND
                ));

        QuestionStep3 question = submission.getQuestion();
        Language language = submission.getLanguage();

        log.info("Step3 제출 내역 조회 완료 - submissionId: {}, language: {}, questionId: {}, partOrder: {}",
                submissionId, language.getCode(), question.getQuestionId(), submission.getPartOrder());

        // STEP 2: Task ID 생성 (UUID)
        String taskId = UUID.randomUUID().toString();
        log.info("Task ID 생성 완료 - taskId: {}", taskId);

        // STEP 3: EvaluationStep3 레코드 생성 (status='PROCESSING')
        EvaluationStep3 evaluation = EvaluationStep3.builder()
                .submission(submission)
                .taskId(taskId)
                .language(language)  // Language Enum 객체 직접 저장 (LanguageConverter가 자동 변환)
                .status("PROCESSING")
                .createdAt(LocalDateTime.now())
                .build();

        evaluationStep3Repository.save(evaluation);
        log.info("Step3 평가 레코드 생성 완료 - evaluationStep3Id: {}, taskId: {}", evaluation.getId(), taskId);

        // STEP 4: S3 Pre-signed URL 생성 (15분 유효)
        String presignedUrl = s3Service.generatePresignedUrl(
                submission.getAudioFileUrl(),
                (int) Duration.ofMinutes(15).toSeconds()
        );
        log.info("S3 Pre-signed URL 생성 완료");

        // STEP 5: 스크립트 선택 (언어에 따라)
        String script = (language == Language.KOREAN)
                ? question.getScriptKorean()
                : question.getScriptEnglish();

        log.info("평가 스크립트 선택 완료 - language: {}, scriptLength: {}",
                language.getCode(), script.length());

        // STEP 6: FastAPI 요청 DTO 생성
        FastAPIEvaluationRequest fastapiRequest = FastAPIEvaluationRequest.builder()
                .taskId(taskId)
                .audioUrl(presignedUrl)
                .script(script)
                .language(language.getCode())
                .callbackUrl(fastapiCallbackUrl)
                .build();

        log.info("FastAPI 요청 DTO 생성 완료 - taskId: {}, callbackUrl: {}", taskId, fastapiCallbackUrl);

        // STEP 7: WebClient로 FastAPI 비동기 호출 (Fire-and-Forget)
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
                            // 평가 상태를 FAILED로 변경
                            evaluation.markAsFailed("FastAPI 요청 실패: " + error.getMessage());
                            evaluationStep3Repository.save(evaluation);
                        }
                );

        log.info("Step3 평가 시작 완료 (비동기) - submissionId: {}, taskId: {}", submissionId, taskId);
    }

    /**
     * FastAPI 평가 완료 콜백 처리 (5번 API)
     * FastAPI에서 평가가 완료되면 호출되는 콜백 API
     * Step2와 Step3 모두 처리
     *
     * @param request FastAPI 콜백 요청 데이터
     */
    @Transactional
    public void handleCallback(CallbackRequest request) {
        log.info("평가 완료 콜백 수신 - taskId: {}, status: {}", request.getTaskId(), request.getStatus());

        // Task ID로 평가 결과 조회 (Step2 먼저, 없으면 Step3)
        java.util.Optional<EvaluationStep2> step2Evaluation = evaluationStep2Repository
                .findByTaskId(request.getTaskId());

        if (step2Evaluation.isPresent()) {
            // Step2 평가 콜백 처리
            handleStep2Callback(step2Evaluation.get(), request);
        } else {
            // Step3 평가 조회
            EvaluationStep3 step3Evaluation = evaluationStep3Repository
                    .findByTaskId(request.getTaskId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "평가 결과를 찾을 수 없습니다. taskId: " + request.getTaskId(),
                            org.skala.crewvoice.exception.ErrorCode.EVALUATION_NOT_FOUND
                    ));

            // Step3 평가 콜백 처리
            handleStep3Callback(step3Evaluation, request);
        }
    }

    /**
     * Step2 평가 콜백 처리
     */
    private void handleStep2Callback(EvaluationStep2 evaluation, CallbackRequest request) {
        log.info("Step2 평가 레코드 조회 완료 - evaluationStep2Id: {}, currentStatus: {}",
                evaluation.getId(), evaluation.getStatus());

        // 평가 상태에 따라 처리
        if ("completed".equalsIgnoreCase(request.getStatus())) {
            // 성공: 점수 업데이트
            CallbackRequest.ScoresDTO scores = request.getScores();

            if (scores == null) {
                log.error("평가 완료되었으나 점수 데이터가 없습니다 - taskId: {}", request.getTaskId());
                evaluation.markAsFailed("점수 데이터 누락");
            } else {
                evaluation.updateScores(
                        scores.getScorePronunciation(),
                        scores.getScorePause(),
                        scores.getScoreIntonation(),
                        scores.getScoreTone(),
                        scores.getScoreSpeed(),
                        scores.getScoreAccent(),
                        scores.getOverallScore(),
                        request.getFeedbackSummary()
                );
                evaluation.markAsCompleted();

                log.info("Step2 평가 점수 업데이트 완료 - taskId: {}, overallScore: {}",
                        request.getTaskId(), scores.getOverallScore());
            }

        } else if ("failed".equalsIgnoreCase(request.getStatus())) {
            // 실패: 에러 메시지 저장
            evaluation.markAsFailed(request.getErrorMessage());
            log.warn("Step2 평가 실패 처리 완료 - taskId: {}, errorMessage: {}",
                    request.getTaskId(), request.getErrorMessage());

        } else {
            log.error("알 수 없는 평가 상태 - taskId: {}, status: {}",
                    request.getTaskId(), request.getStatus());
            evaluation.markAsFailed("알 수 없는 상태: " + request.getStatus());
        }

        // 저장
        evaluationStep2Repository.save(evaluation);
        log.info("Step2 평가 콜백 처리 완료 - taskId: {}, finalStatus: {}",
                request.getTaskId(), evaluation.getStatus());
    }

    /**
     * Step3 평가 콜백 처리
     */
    private void handleStep3Callback(EvaluationStep3 evaluation, CallbackRequest request) {
        log.info("Step3 평가 레코드 조회 완료 - evaluationStep3Id: {}, currentStatus: {}, language: {}",
                evaluation.getId(), evaluation.getStatus(), evaluation.getLanguage().getCode());

        // 평가 상태에 따라 처리
        if ("completed".equalsIgnoreCase(request.getStatus())) {
            // 성공: 점수 업데이트
            CallbackRequest.ScoresDTO scores = request.getScores();

            if (scores == null) {
                log.error("평가 완료되었으나 점수 데이터가 없습니다 - taskId: {}", request.getTaskId());
                evaluation.markAsFailed("점수 데이터 누락");
            } else {
                evaluation.updateScores(
                        scores.getScorePronunciation(),
                        scores.getScorePause(),
                        scores.getScoreIntonation(),
                        scores.getScoreTone(),
                        scores.getScoreSpeed(),
                        scores.getScoreAccent(),
                        scores.getOverallScore(),
                        request.getFeedbackSummary()
                );
                evaluation.markAsCompleted();

                log.info("Step3 평가 점수 업데이트 완료 - taskId: {}, language: {}, overallScore: {}",
                        request.getTaskId(), evaluation.getLanguage().getCode(), scores.getOverallScore());
            }

        } else if ("failed".equalsIgnoreCase(request.getStatus())) {
            // 실패: 에러 메시지 저장
            evaluation.markAsFailed(request.getErrorMessage());
            log.warn("Step3 평가 실패 처리 완료 - taskId: {}, language: {}, errorMessage: {}",
                    request.getTaskId(), evaluation.getLanguage().getCode(), request.getErrorMessage());

        } else {
            log.error("알 수 없는 평가 상태 - taskId: {}, status: {}",
                    request.getTaskId(), request.getStatus());
            evaluation.markAsFailed("알 수 없는 상태: " + request.getStatus());
        }

        // 저장
        evaluationStep3Repository.save(evaluation);
        log.info("Step3 평가 콜백 처리 완료 - taskId: {}, language: {}, finalStatus: {}",
                request.getTaskId(), evaluation.getLanguage().getCode(), evaluation.getStatus());
    }
}