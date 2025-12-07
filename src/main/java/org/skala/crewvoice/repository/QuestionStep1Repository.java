package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.QuestionStep1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface QuestionStep1Repository extends JpaRepository<QuestionStep1, Long> {

    /**
     * 특정 상태의 STEP1 문제 조회 (question_id 기준 정렬, 페이지네이션)
     *
     * @param status   문제 상태 (ACTIVE, INACTIVE 등)
     * @param pageable 페이지 정보
     * @return STEP1 문제 페이지
     */
    @Query("SELECT q FROM QuestionStep1 q WHERE q.status = :status ORDER BY q.questionId ASC")
    org.springframework.data.domain.Page<QuestionStep1> findByStatus(@Param("status") String status, org.springframework.data.domain.Pageable pageable);

    /**
     * STEP1 랜덤 문제 조회 (제외할 ID 리스트 기준)
     *
     * @param excludedIds 제외할 문제 ID 리스트
     * @param isEmpty     제외 리스트가 비어있는지 여부
     * @return 랜덤 STEP1 문제
     */
    @Query(value = "SELECT * FROM questions_step1 WHERE status = 'ACTIVE' AND (:isEmpty = true OR question_id NOT IN :excludedIds) ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    java.util.Optional<QuestionStep1> findRandomQuestion(@Param("excludedIds") java.util.List<Long> excludedIds, @Param("isEmpty") boolean isEmpty);

    /**
     * STEP1 문제의 최종 수정 일시 조회
     *
     * @return 가장 최근 수정된 문제의 updated_at
     */
    @Query("SELECT MAX(q.updatedAt) FROM QuestionStep1 q")
    OffsetDateTime findLatestUpdatedAt();

    /**
     * 특정 상태의 STEP1 문제 개수 조회
     *
     * @param status 문제 상태 (ACTIVE, INACTIVE)
     * @return 해당 상태의 문제 개수
     */
    long countByStatus(String status);
}