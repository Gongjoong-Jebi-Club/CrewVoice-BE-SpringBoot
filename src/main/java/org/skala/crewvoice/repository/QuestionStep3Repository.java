package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.QuestionStep3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}