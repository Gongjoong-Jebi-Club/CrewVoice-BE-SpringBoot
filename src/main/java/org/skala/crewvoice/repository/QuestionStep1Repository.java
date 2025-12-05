package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.QuestionStep1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

/**
 * STEP1 문제 은행 Repository
 */
@Repository
public interface QuestionStep1Repository extends JpaRepository<QuestionStep1, Long> {

    /**
     * STEP1 문제의 최종 수정 일시 조회
     *
     * @return 가장 최근 수정된 문제의 updated_at
     */
    @Query("SELECT MAX(q.updatedAt) FROM QuestionStep1 q")
    OffsetDateTime findLatestUpdatedAt();
}