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
     * 모든 평가 조회 (생성일 기준 최신순)
     * 관리자가 모든 평가를 조회할 때 사용
     */
    List<Evaluation> findAllByOrderByCreatedAtDesc();

    /**
     * 현재 진행 중인 평가 목록 조회 (모든 사용자용)
     * 현재 시간이 start_date와 end_date 사이에 있는 평가들을 시작일 기준 오름차순으로 조회
     *
     * @param now 현재 시간
     * @return 진행 중인 평가 목록
     */
    @Query("SELECT e FROM Evaluation e " +
            "WHERE e.startDate <= :now " +
            "AND e.endDate >= :now " +
            "ORDER BY e.startDate ASC")
    List<Evaluation> findActiveEvaluations(@Param("now") OffsetDateTime now);

    /**
     * 현재 진행 중인 평가 목록 조회 - CREW 사용자용 (배정된 평가만)
     * 사용자 생성 이후에 생성된 평가만 조회
     *
     * @param now 현재 시간
     * @param userCreatedAt 사용자 생성 시간
     * @return 진행 중인 평가 목록 (배정된 평가만)
     */
    @Query("SELECT e FROM Evaluation e " +
            "WHERE e.startDate <= :now " +
            "AND e.endDate >= :now " +
            "AND e.createdAt > :userCreatedAt " +
            "ORDER BY e.startDate ASC")
    List<Evaluation> findActiveEvaluationsForCrew(
            @Param("now") OffsetDateTime now,
            @Param("userCreatedAt") OffsetDateTime userCreatedAt
    );
}