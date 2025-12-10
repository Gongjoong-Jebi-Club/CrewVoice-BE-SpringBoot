package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.EvaluationStep3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    /**
     * Task ID로 Step3 평가 결과 조회 (콜백 처리용)
     *
     * @param taskId Task ID
     * @return Step3 평가 결과 (Optional)
     */
    @Query("SELECT e FROM EvaluationStep3 e " +
            "WHERE e.taskId = :taskId")
    Optional<EvaluationStep3> findByTaskId(@Param("taskId") String taskId);
}
