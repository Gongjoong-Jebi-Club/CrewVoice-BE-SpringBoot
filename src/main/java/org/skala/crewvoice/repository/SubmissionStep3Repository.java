package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.Language;
import org.skala.crewvoice.domain.SubmissionStep3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionStep3Repository extends JpaRepository<SubmissionStep3, Long> {

    /**
     * 특정 사용자와 평가 ID로 Step3 제출 기록 조회 (part_order 순으로 정렬)
     *
     * @param userId 사용자 ID
     * @param evaluationId 평가 ID
     * @return Step3 제출 기록 리스트 (part_order 순)
     */
    @Query("SELECT s FROM SubmissionStep3 s " +
            "WHERE s.user.id = :userId AND s.evaluation.evaluationId = :evaluationId " +
            "ORDER BY s.partOrder ASC")
    List<SubmissionStep3> findByUserIdAndEvaluationIdOrderByPartOrder(
            @Param("userId") Long userId,
            @Param("evaluationId") Long evaluationId
    );

    /**
     * 특정 사용자의 특정 평가, 특정 part_order에 대한 STEP3 제출 내역 조회
     * N+1 문제 방지를 위해 JOIN FETCH 사용
     */
    @Query("SELECT s FROM SubmissionStep3 s " +
            "JOIN FETCH s.user u " +
            "JOIN FETCH s.evaluation e " +
            "WHERE s.user.id = :userId " +
            "AND s.evaluation.evaluationId = :evaluationId " +
            "AND s.partOrder = :partOrder")
    Optional<SubmissionStep3> findByUserIdAndEvaluationIdAndPartOrder(
            @Param("userId") Long userId,
            @Param("evaluationId") Long evaluationId,
            @Param("partOrder") Integer partOrder
    );

    /**
     * Submission ID로 Step3 제출 기록 조회 (JOIN FETCH로 N+1 문제 방지)
     * Step2와 동일한 패턴
     *
     * @param submissionId 제출 ID
     * @return Step3 제출 기록 (Optional)
     */
    @Query("SELECT s FROM SubmissionStep3 s " +
            "JOIN FETCH s.question q " +
            "WHERE s.submissionId = :submissionId")
    Optional<SubmissionStep3> findByIdWithQuestion(@Param("submissionId") Long submissionId);

    /**
     * 특정 사용자와 평가 ID, 언어로 Step3 제출 기록 조회
     *
     * @param userId 사용자 ID
     * @param evaluationId 평가 ID
     * @param language 언어 (KOREAN/ENGLISH)
     * @return Step3 제출 기록 (Optional)
     */
    @Query("SELECT s FROM SubmissionStep3 s " +
            "WHERE s.user.id = :userId " +
            "AND s.evaluation.evaluationId = :evaluationId " +
            "AND s.language = :language")
    Optional<SubmissionStep3> findByUserIdAndEvaluationIdAndLanguage(
            @Param("userId") Long userId,
            @Param("evaluationId") Long evaluationId,
            @Param("language") Language language
    );

    /**
     * 특정 사용자의 특정 평가, 특정 partOrder와 언어에 대한 STEP3 제출 내역 조회
     * (제출 API용)
     */
    @Query("SELECT s FROM SubmissionStep3 s " +
            "WHERE s.user.id = :userId " +
            "AND s.evaluation.evaluationId = :evaluationId " +
            "AND s.partOrder = :partOrder " +
            "AND s.language = :language")
    Optional<SubmissionStep3> findByUserIdAndEvaluationIdAndPartOrderAndLanguage(
            @Param("userId") Long userId,
            @Param("evaluationId") Long evaluationId,
            @Param("partOrder") Integer partOrder,
            @Param("language") Language language
    );
}
