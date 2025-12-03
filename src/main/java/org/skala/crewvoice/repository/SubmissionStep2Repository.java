package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.SubmissionStep2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubmissionStep2Repository extends JpaRepository<SubmissionStep2, Long> {

    /**
     * 특정 사용자의 특정 평가에 대한 STEP2 제출 내역 조회
     * N+1 문제 방지를 위해 JOIN FETCH 사용
     */
    @Query("SELECT s FROM SubmissionStep2 s " +
            "JOIN FETCH s.user u " +
            "JOIN FETCH s.evaluation e " +
            "WHERE s.user.id = :userId " +
            "AND s.evaluation.id = :evaluationId")
    Optional<SubmissionStep2> findByUserIdAndEvaluationId(
            @Param("userId") Long userId,
            @Param("evaluationId") Long evaluationId
    );
}