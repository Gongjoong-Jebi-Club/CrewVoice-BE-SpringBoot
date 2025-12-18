package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.FinalGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FinalGradeRepository extends JpaRepository<FinalGrade, Long> {

    /**
     * 특정 사용자의 최종 성적을 최신 응시순(created_at 내림차순)으로 조회
     *
     * @param userId 사용자 ID
     * @return 최종 성적 리스트 (최신순)
     */
    @Query("SELECT fg FROM FinalGrade fg JOIN FETCH fg.evaluation WHERE fg.user.id = :userId ORDER BY fg.createdAt DESC")
    List<FinalGrade> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    /**
     * 특정 사용자와 평가 ID로 최종 성적 조회
     *
     * @param userId 사용자 ID
     * @param evaluationId 평가 ID
     * @return 최종 성적
     */
    @Query("SELECT fg FROM FinalGrade fg WHERE fg.user.id = :userId AND fg.evaluation.evaluationId = :evaluationId")
    FinalGrade findByUserIdAndEvaluationId(@Param("userId") Long userId, @Param("evaluationId") Long evaluationId);

    /**
     * 특정 평가의 완료자 수 조회
     */
    @Query("SELECT COUNT(fg) FROM FinalGrade fg WHERE fg.evaluation.evaluationId = :evaluationId")
    Integer countByEvaluationId(@Param("evaluationId") Long evaluationId);

    /**
     * 특정 사용자의 가장 최신 EXAM 타입 최종 성적을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 가장 최신 EXAM 타입 최종 성적 (Optional)
     */
    @Query("SELECT fg FROM FinalGrade fg " +
            "JOIN FETCH fg.evaluation e " +
            "WHERE fg.user.id = :userId " +
            "AND e.evaluationType = 'EXAM' " +
            "ORDER BY fg.createdAt DESC " +
            "LIMIT 1")
    Optional<FinalGrade> findLatestByUserId(@Param("userId") Long userId);

    /**
     * Step2 한국어 점수만 업데이트 (Race Condition 방지)
     */
    @Modifying
    @Query("UPDATE FinalGrade fg SET fg.step2KorScore = :score WHERE fg.user.id = :userId AND fg.evaluation.evaluationId = :evaluationId")
    int updateStep2KorScore(@Param("userId") Long userId, @Param("evaluationId") Long evaluationId, @Param("score") BigDecimal score);

    /**
     * Step2 영어 점수만 업데이트 (Race Condition 방지)
     */
    @Modifying
    @Query("UPDATE FinalGrade fg SET fg.step2EngScore = :score WHERE fg.user.id = :userId AND fg.evaluation.evaluationId = :evaluationId")
    int updateStep2EngScore(@Param("userId") Long userId, @Param("evaluationId") Long evaluationId, @Param("score") BigDecimal score);

    /**
     * Step3 한국어 점수만 업데이트 (Race Condition 방지)
     */
    @Modifying
    @Query("UPDATE FinalGrade fg SET fg.step3KorScore = :score WHERE fg.user.id = :userId AND fg.evaluation.evaluationId = :evaluationId")
    int updateStep3KorScore(@Param("userId") Long userId, @Param("evaluationId") Long evaluationId, @Param("score") BigDecimal score);

    /**
     * Step3 영어 점수만 업데이트 (Race Condition 방지)
     */
    @Modifying
    @Query("UPDATE FinalGrade fg SET fg.step3EngScore = :score WHERE fg.user.id = :userId AND fg.evaluation.evaluationId = :evaluationId")
    int updateStep3EngScore(@Param("userId") Long userId, @Param("evaluationId") Long evaluationId, @Param("score") BigDecimal score);

}