package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.SubmissionStep1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionStep1Repository extends JpaRepository<SubmissionStep1, Long> {

    /**
     * 특정 사용자와 평가 ID로 Step1 오답 기록 조회 (is_correct = false, question_order 순으로 정렬)
     *
     * @param userId 사용자 ID
     * @param evaluationId 평가 ID
     * @return Step1 오답 기록 리스트 (question_order 순)
     */
    @Query("SELECT s FROM SubmissionStep1 s " +
            "JOIN FETCH s.question " +
            "WHERE s.user.id = :userId AND s.evaluation.id = :evaluationId AND s.isCorrect = false " +
            "ORDER BY s.questionOrder ASC")
    List<SubmissionStep1> findByUserIdAndEvaluationIdOrderByQuestionOrder(
            @Param("userId") Long userId,
            @Param("evaluationId") Long evaluationId
    );

    /**
     * 특정 사용자와 평가 ID로 Step1 제출 개수 조회
     *
     * @param userId 사용자 ID
     * @param evaluationId 평가 ID
     * @return Step1 제출 개수
     */
    @Query("SELECT COUNT(s) FROM SubmissionStep1 s " +
            "WHERE s.user.id = :userId AND s.evaluation.evaluationId = :evaluationId")
    long countByUserIdAndEvaluationId(
            @Param("userId") Long userId,
            @Param("evaluationId") Long evaluationId
    );

    /**
     * 특정 사용자와 평가 ID로 Step1 정답 개수 조회
     *
     * @param userId 사용자 ID
     * @param evaluationId 평가 ID
     * @return Step1 정답 개수
     */
    @Query("SELECT COUNT(s) FROM SubmissionStep1 s " +
            "WHERE s.user.id = :userId AND s.evaluation.evaluationId = :evaluationId AND s.isCorrect = true")
    long countCorrectByUserIdAndEvaluationId(
            @Param("userId") Long userId,
            @Param("evaluationId") Long evaluationId
    );
}
