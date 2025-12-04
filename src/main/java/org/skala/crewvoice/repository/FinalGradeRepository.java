package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.FinalGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
}