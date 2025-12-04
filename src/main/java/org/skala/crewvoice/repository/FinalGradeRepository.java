package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.FinalGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinalGradeRepository extends JpaRepository<FinalGrade, Long> {

    /**
     * 특정 평가의 완료자 수 조회
     */
    @Query("SELECT COUNT(fg) FROM FinalGrade fg WHERE fg.evaluation.evaluationId = :evaluationId")
    Integer countByEvaluationId(@Param("evaluationId") Long evaluationId);

    /**
     * 특정 사용자의 특정 평가 성적 조회
     * LEFT JOIN FETCH를 통해 N+1 문제 방지
     */
    @Query("SELECT fg FROM FinalGrade fg " +
            "JOIN FETCH fg.user " +
            "JOIN FETCH fg.evaluation " +
            "WHERE fg.user.id = :userId " +
            "AND fg.evaluation.evaluationId = :evaluationId")
    Optional<FinalGrade> findByUserIdAndEvaluationId(
            @Param("userId") Long userId,
            @Param("evaluationId") Long evaluationId
    );
}