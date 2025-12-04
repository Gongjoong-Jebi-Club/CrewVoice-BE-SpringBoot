package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.EvaluationStep3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationStep3Repository extends JpaRepository<EvaluationStep3, Long> {

    /**
     * 특정 제출 ID로 Step3 평가 결과 조회 (한국어, 영어 2개)
     *
     * @param submissionId 제출 ID
     * @return Step3 평가 결과 리스트
     */
    @Query("SELECT e FROM EvaluationStep3 e " +
            "WHERE e.submission.id = :submissionId")
    List<EvaluationStep3> findBySubmissionId(@Param("submissionId") Long submissionId);
}
