package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.SubmissionStep2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionStep2Repository extends JpaRepository<SubmissionStep2, Long> {

    /**
     * 특정 사용자와 평가 ID로 Step2 제출 기록 조회 (한국어/영어 각각 존재 가능)
     *
     * @param userId 사용자 ID
     * @param evaluationId 평가 ID
     * @return Step2 제출 기록 리스트
     */
    @Query("SELECT s FROM SubmissionStep2 s " +
            "WHERE s.user.id = :userId AND s.evaluation.id = :evaluationId")
    List<SubmissionStep2> findByUserIdAndEvaluationId(
            @Param("userId") Long userId,
            @Param("evaluationId") Long evaluationId
    );
}
