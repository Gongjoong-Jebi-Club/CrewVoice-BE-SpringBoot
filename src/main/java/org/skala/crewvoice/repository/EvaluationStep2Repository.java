package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.EvaluationStep2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationStep2Repository extends JpaRepository<EvaluationStep2, Long> {

    /**
     * 특정 제출 ID로 Step2 평가 결과 조회
     *
     * @param submissionId 제출 ID
     * @return Step2 평가 결과 리스트
     */
    @Query("SELECT e FROM EvaluationStep2 e " +
            "WHERE e.submission.id = :submissionId")
    List<EvaluationStep2> findBySubmissionId(@Param("submissionId") Long submissionId);

    /**
     * Task ID로 Step2 평가 결과 조회 (FastAPI callback에서 사용)
     *
     * @param taskId FastAPI 평가 작업 ID (UUID)
     * @return Step2 평가 결과 (Optional)
     */
    Optional<EvaluationStep2> findByTaskId(String taskId);
}
