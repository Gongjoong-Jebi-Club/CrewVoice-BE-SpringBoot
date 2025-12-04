package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.QuestionStep2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * STEP2 문제 Repository
 */
@Repository
public interface QuestionStep2Repository extends JpaRepository<QuestionStep2, Long> {

    /**
     * 모든 STEP2 문제 조회 (생성일 기준 정렬)
     * @return STEP2 문제 리스트
     */
    List<QuestionStep2> findAllByOrderByCreatedAtAsc();

    // 랜덤 조회
    @Query(value = "SELECT * FROM questions_step2 WHERE (:isEmpty = true OR question_id NOT IN :excludedIds) ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<QuestionStep2> findRandomQuestion(@Param("excludedIds") List<Long> excludedIds, @Param("isEmpty") boolean isEmpty);

    /**
     * STEP2 문제의 최종 수정 일시 조회
     *
     * @return 가장 최근 수정된 문제의 updated_at
     */
    @Query("SELECT MAX(q.updatedAt) FROM QuestionStep2 q")
    OffsetDateTime findLatestUpdatedAt();
}