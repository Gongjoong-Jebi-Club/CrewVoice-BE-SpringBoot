package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 평가(시험) Repository
 */
@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    /**
     * 현재 진행 중인 평가 조회
     */
    @Query("SELECT e FROM Evaluation e WHERE " +
            ":now BETWEEN e.startDate AND e.endDate " +
            "ORDER BY e.startDate DESC")
    List<Evaluation> findActiveEvaluations(@Param("now") OffsetDateTime now);
}