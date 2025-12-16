package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.EvaluationTargetUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    /**
     * 특정 사용자와 평가에 대한 EvaluationTargetUser 조회
     *
     * @param userId 사용자 ID
     * @param evaluationId 평가 ID
     * @return EvaluationTargetUser
     */
    @Query("SELECT etu FROM EvaluationTargetUser etu " +
           "WHERE etu.user.id = :userId AND etu.evaluation.evaluationId = :evaluationId")
    Optional<EvaluationTargetUser> findByUserIdAndEvaluationId(
            @Param("userId") Long userId,
            @Param("evaluationId") Long evaluationId);

    /**
     * 특정 사용자가 특정 평가를 응시했는지 여부 조회
     *
     * @param userId 사용자 ID
     * @param evaluationId 평가 ID
     * @return 응시 여부 (null이면 배정되지 않은 평가)
     */
    @Query("SELECT etu.isTaken FROM EvaluationTargetUser etu " +
           "WHERE etu.user.id = :userId AND etu.evaluation.evaluationId = :evaluationId")
    Optional<Boolean> findIsTakenByUserIdAndEvaluationId(
            @Param("userId") Long userId,
            @Param("evaluationId") Long evaluationId);
}
