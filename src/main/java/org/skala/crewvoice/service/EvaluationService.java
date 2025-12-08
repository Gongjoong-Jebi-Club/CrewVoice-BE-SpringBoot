package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.domain.Evaluation;
import org.skala.crewvoice.domain.EvaluationTargetUser;
import org.skala.crewvoice.domain.EvaluationType;
import org.skala.crewvoice.domain.User;
import org.skala.crewvoice.dto.request.EvaluationCreateRequest;
import org.skala.crewvoice.dto.response.*;
import org.skala.crewvoice.repository.EvaluationRepository;
import org.skala.crewvoice.repository.EvaluationTargetUserRepository;
import org.skala.crewvoice.repository.FinalGradeRepository;
import org.skala.crewvoice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.time.OffsetDateTime;

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
}