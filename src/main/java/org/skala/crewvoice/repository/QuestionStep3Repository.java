package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.QuestionStep3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * STEP3 문제 Repository
 */
@Repository
public interface QuestionStep3Repository extends JpaRepository<QuestionStep3, Long> {

    /**
     * 모든 STEP3 문제 조회 (생성일 기준 정렬)
     * @return STEP3 문제 리스트
     */
    List<QuestionStep3> findAllByOrderByCreatedAtAsc();

    // 랜덤 조회
    @Query(value = "SELECT * FROM questions_step3 WHERE (:isEmpty = true OR question_id NOT IN :excludedIds) ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<QuestionStep3> findRandomQuestion(@Param("excludedIds") List<Long> excludedIds, @Param("isEmpty") boolean isEmpty);
}