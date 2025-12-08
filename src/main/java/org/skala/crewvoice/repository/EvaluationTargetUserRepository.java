package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.EvaluationTargetUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationTargetUserRepository extends JpaRepository<EvaluationTargetUser, Long> {

    /**
     * 특정 평가의 대상자 목록 조회
     *
     * @param evaluationId 평가 ID
     * @return 평가 대상자 목록
     */
    List<EvaluationTargetUser> findByEvaluationEvaluationId(Long evaluationId);

    /**
     * 특정 사용자가 대상인 평가 목록 조회
     *
     * @param userId 사용자 ID
     * @return 사용자가 대상인 평가 목록
     */
    List<EvaluationTargetUser> findByUserId(Long userId);
}
