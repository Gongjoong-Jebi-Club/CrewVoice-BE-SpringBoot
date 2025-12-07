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
     * 특정 상태의 STEP2 문제 조회 (question_id 기준 정렬, 페이지네이션)
     * @param status 문제 상태 (ACTIVE, INACTIVE 등)
     * @param pageable 페이지 정보
     * @return STEP2 문제 페이지
     */
    @Query("SELECT q FROM QuestionStep2 q WHERE q.status = :status ORDER BY q.questionId ASC")
    org.springframework.data.domain.Page<QuestionStep2> findByStatus(@Param("status") String status, org.springframework.data.domain.Pageable pageable);

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

    /**
     * 특정 상태의 STEP2 문제 개수 조회
     *
     * @param status 문제 상태 (ACTIVE, INACTIVE)
     * @return 해당 상태의 문제 개수
     */
    long countByStatus(String status);
}