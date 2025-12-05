package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.QuestionStep3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * STEP3 문제 Repository
 */
@Repository
public interface QuestionStep3Repository extends JpaRepository<QuestionStep3, Long> {

    /**
     * 특정 상태의 STEP3 문제 조회 (question_id 기준 정렬, 페이지네이션)
     * @param status 문제 상태 (ACTIVE, INACTIVE 등)
     * @param pageable 페이지 정보
     * @return STEP3 문제 페이지
     */
    @Query("SELECT q FROM QuestionStep3 q WHERE q.status = :status ORDER BY q.questionId ASC")
    org.springframework.data.domain.Page<QuestionStep3> findByStatus(@Param("status") String status, org.springframework.data.domain.Pageable pageable);

    // 랜덤 조회
    @Query(value = "SELECT * FROM questions_step3 WHERE (:isEmpty = true OR question_id NOT IN :excludedIds) ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<QuestionStep3> findRandomQuestion(@Param("excludedIds") List<Long> excludedIds, @Param("isEmpty") boolean isEmpty);
    /**
     * STEP3 문제의 최종 수정 일시 조회
     *
     * @return 가장 최근 수정된 문제의 updated_at
     */
    @Query("SELECT MAX(q.updatedAt) FROM QuestionStep3 q")
    OffsetDateTime findLatestUpdatedAt();


}
