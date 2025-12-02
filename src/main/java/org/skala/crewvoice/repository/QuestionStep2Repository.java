package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.QuestionStep2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}