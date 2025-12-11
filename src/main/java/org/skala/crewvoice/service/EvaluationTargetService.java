package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.domain.Evaluation;
import org.skala.crewvoice.domain.EvaluationTargetUser;
import org.skala.crewvoice.domain.User;
import org.skala.crewvoice.repository.EvaluationTargetUserRepository;
import org.skala.crewvoice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 평가 대상자 관리 전담 서비스
 * 평가 대상자 자동 등록 및 조회 기능을 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EvaluationTargetService {

    private final UserRepository userRepository;
    private final EvaluationTargetUserRepository evaluationTargetUserRepository;

    /**
     * 평가 대상자 자동 등록
     * 현재 ACTIVE 상태인 모든 CREW 사용자를 평가 대상자로 등록합니다.
     *
     * @param evaluation 평가 엔티티
     * @return 등록된 대상자 수
     */
    @Transactional
    public int registerTargetUsers(Evaluation evaluation) {
        log.info("평가 대상자 자동 등록 시작 - evaluationId: {}", evaluation.getEvaluationId());

        // 현재 시점의 모든 ACTIVE CREW 사용자 조회
        List<User> crewUsers = userRepository.findAllActiveCrewMembers();
        log.info("조회된 CREW 사용자 수: {}", crewUsers.size());

        if (crewUsers.isEmpty()) {
            log.warn("등록할 CREW 사용자가 없습니다. 평가 대상자가 등록되지 않았습니다.");
            return 0;
        }

        // 평가 대상자 엔티티 생성
        List<EvaluationTargetUser> targetUsers = crewUsers.stream()
                .map(user -> {
                    log.debug("평가 대상자 추가: userId={}, userName={}", user.getId(), user.getName());
                    return EvaluationTargetUser.builder()
                            .evaluation(evaluation)
                            .user(user)
                            .build();
                })
                .collect(Collectors.toList());

        // 일괄 저장
        List<EvaluationTargetUser> savedTargetUsers = evaluationTargetUserRepository.saveAll(targetUsers);
        log.info("평가 대상자 자동 등록 완료 - 대상자 수: {}", savedTargetUsers.size());

        return savedTargetUsers.size();
    }

    /**
     * 특정 평가의 대상자 수 조회
     *
     * @param evaluationId 평가 ID
     * @return 대상자 수
     */
    public int getTargetCount(Long evaluationId) {
        return evaluationTargetUserRepository
                .findByEvaluationEvaluationId(evaluationId)
                .size();
    }

    /**
     * 특정 평가의 대상자 목록 조회
     *
     * @param evaluationId 평가 ID
     * @return 대상자 목록
     */
    public List<EvaluationTargetUser> getTargetUsers(Long evaluationId) {
        return evaluationTargetUserRepository
                .findByEvaluationEvaluationId(evaluationId);
    }
}
