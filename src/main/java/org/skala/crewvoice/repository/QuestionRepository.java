package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findByStep(int step, Pageable pageable);
}
