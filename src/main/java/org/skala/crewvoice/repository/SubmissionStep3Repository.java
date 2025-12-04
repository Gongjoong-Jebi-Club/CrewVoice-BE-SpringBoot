package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.SubmissionStep3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubmissionStep3Repository extends JpaRepository<SubmissionStep3, Long> {

    /**
     * 특정 사용자의 특정 평가, 특정 part_order에 대한 STEP3 제출 내역 조회
     * N+1 문제 방지를 위해 JOIN FETCH 사용
     */
    @Query("SELECT s FROM SubmissionStep3 s " +
            "JOIN FETCH s.user u " +
            "JOIN FETCH s.evaluation e " +
            "WHERE s.user.id = :userId " +
            "AND s.evaluation.id = :evaluationId " +
            "AND s.partOrder = :partOrder")
    Optional<SubmissionStep3> findByUserIdAndEvaluationIdAndPartOrder(
            @Param("userId") Long userId,
            @Param("evaluationId") Long evaluationId,
            @Param("partOrder") Integer partOrder
    );
}