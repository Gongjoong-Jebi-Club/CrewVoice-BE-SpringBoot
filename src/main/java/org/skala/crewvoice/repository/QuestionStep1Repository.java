package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.QuestionStep1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionStep1Repository extends JpaRepository<QuestionStep1, Long> {
}
