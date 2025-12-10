package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.Language;
import org.skala.crewvoice.domain.SubmissionStep2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionStep2Repository extends JpaRepository<SubmissionStep2, Long> {

    /**
     * 특정 사용자와 평가 ID로 Step2 제출 기록 조회
     *
     * @param userId 사용자 ID
     * @param evaluationId 평가 ID
     * @return Step2 제출 기록 리스트
     */
    @Query("SELECT s FROM SubmissionStep2 s " +
            "WHERE s.user.id = :userId AND s.evaluation.evaluationId = :evaluationId")
    List<SubmissionStep2> findByUserIdAndEvaluationId(
            @Param("userId") Long userId,
            @Param("evaluationId") Long evaluationId
    );

    /**
     * Submission ID로 Step2 제출 기록 조회 (JOIN FETCH로 N+1 문제 방지)
     *
     * @param submissionId 제출 ID
     * @return Step2 제출 기록 (Optional)
     */
    @Query("SELECT s FROM SubmissionStep2 s " +
            "JOIN FETCH s.question q " +
            "WHERE s.submissionId = :submissionId")
    Optional<SubmissionStep2> findByIdWithQuestion(@Param("submissionId") Long submissionId);

    /**
     * 특정 사용자와 평가 ID, 언어로 Step2 제출 기록 조회
     *
     * @param userId 사용자 ID
     * @param evaluationId 평가 ID
     * @param language 언어 (KOREAN/ENGLISH)
     * @return Step2 제출 기록 (Optional)
     */
    @Query("SELECT s FROM SubmissionStep2 s " +
            "WHERE s.user.id = :userId " +
            "AND s.evaluation.evaluationId = :evaluationId " +
            "AND s.language = :language")
    Optional<SubmissionStep2> findByUserIdAndEvaluationIdAndLanguage(
            @Param("userId") Long userId,
            @Param("evaluationId") Long evaluationId,
            @Param("language") Language language
    );
}
