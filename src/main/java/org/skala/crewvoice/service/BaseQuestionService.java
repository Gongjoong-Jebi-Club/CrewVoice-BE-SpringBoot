package org.skala.crewvoice.service;

import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Question 관련 서비스의 공통 로직을 제공하는 유틸리티 클래스
 * 현재 Step2와 Step3의 공통 로직에서 활용 중
 */
public abstract class BaseQuestionService {

    private static final int PAGE_SIZE = 10;

    /**
     * 페이지네이션을 적용한 문제 목록 조회 (공통 로직)
     *
     * @param pageNumber 페이지 번호 (1-based)
     * @param repository JPA Repository
     * @param mapper     Entity → DTO 변환 함수
     * @return DTO 리스트
     */
    protected <T, R> List<R> getQuestions(int pageNumber, JpaRepository<T, Long> repository, Function<T, R> mapper) {
        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE);
        Page<T> questionsPage = repository.findAll(pageable);

        return questionsPage.getContent()
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    /**
     * ID로 특정 문제 조회 (공통 로직)
     *
     * @param questionId 문제 ID
     * @param repository JPA Repository
     * @param mapper     Entity → DTO 변환 함수
     * @return DTO
     */
    protected <T, R> R getQuestionById(Long questionId, JpaRepository<T, Long> repository, Function<T, R> mapper) {
        T question = repository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.QUESTION_NOT_FOUND));

        return mapper.apply(question);
    }
}
