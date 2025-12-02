package org.skala.crewvoice.repository;

import org.skala.crewvoice.domain.QuestionStep2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionStep2Repository extends JpaRepository<QuestionStep2, Long> {

    @Query(value = "SELECT * FROM questions_step2 WHERE (:isEmpty = true OR question_id NOT IN :excludedIds) ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<QuestionStep2> findRandomQuestion(@Param("excludedIds") List<Long> excludedIds, @Param("isEmpty") boolean isEmpty);
}
