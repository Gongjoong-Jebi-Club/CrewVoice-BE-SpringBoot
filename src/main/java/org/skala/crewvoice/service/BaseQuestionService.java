package org.skala.crewvoice.service;

import org.skala.crewvoice.dto.response.QuestionEditResponse;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
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

    /**
     * 문제 추가/수정 (공통 로직)
     *
     * @param questionId      문제 ID (null이면 추가, 값이 있으면 수정)
     * @param repository      JPA Repository
     * @param entityCreator   새 Entity 생성 함수 (추가 시 사용)
     * @param updateLogic     기존 Entity 업데이트 로직 (수정 시 사용)
     * @return QuestionEditResponse (저장/수정된 문제 ID 포함)
     */
    protected <T> QuestionEditResponse saveOrUpdateQuestion(
            Long questionId,
            JpaRepository<T, Long> repository,
            Supplier<T> entityCreator,
            Consumer<T> updateLogic
    ) {
        if (questionId == null) {
            // 문제 추가
            T newQuestion = entityCreator.get();
            T savedQuestion = repository.save(newQuestion);

            // Entity에서 ID를 가져오기 위한 리플렉션 (모든 Question 엔티티는 getId() 메서드를 가짐)
            try {
                Long id = (Long) savedQuestion.getClass().getMethod("getId").invoke(savedQuestion);
                return new QuestionEditResponse(id);
            } catch (Exception e) {
                throw new RuntimeException("Failed to get question ID", e);
            }
        } else {
            // 문제 수정
            T question = repository.findById(questionId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.QUESTION_NOT_FOUND));

            updateLogic.accept(question);

            return new QuestionEditResponse(questionId);
        }
    }
}
