package org.skala.crewvoice.service;

import org.skala.crewvoice.domain.FinalGrade;
import org.skala.crewvoice.domain.User;
import org.skala.crewvoice.dto.response.MockExamDetailResponse;
import org.skala.crewvoice.dto.response.MockExamResultResponse;
import org.skala.crewvoice.dto.response.MockExamScoresResponse;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.repository.FinalGradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MockExamResultService {

    private final FinalGradeRepository finalGradeRepository;

    public MockExamResultService(FinalGradeRepository finalGradeRepository) {
        this.finalGradeRepository = finalGradeRepository;
    }

    /**
     * 현재 로그인한 사용자의 모의고사 결과를 최신 응시순으로 조회
     *
     * @param user 현재 로그인한 사용자
     * @return 모의고사 결과 리스트 (최신순)
     */
    public List<MockExamResultResponse> getMyMockExamResults(User user) {
        List<FinalGrade> finalGrades = finalGradeRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        return finalGrades.stream()
                .map(MockExamResultResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 평가의 상세 점수 조회
     *
     * @param user 현재 로그인한 사용자
     * @param evaluationId 평가 ID
     * @return 모의고사 상세 점수
     */
    public MockExamDetailResponse getMockExamDetail(User user, Long evaluationId) {
        FinalGrade finalGrade = finalGradeRepository.findByUserIdAndEvaluationId(user.getId(), evaluationId);

        if (finalGrade == null) {
            throw new EntityNotFoundException(ErrorCode.EVALUATION_NOT_FOUND);
        }

        return MockExamDetailResponse.from(finalGrade);
    }

    /**
     * 특정 모의고사의 점수 조회
     *
     * @param user 현재 로그인한 사용자
     * @param evaluationId 평가 ID
     * @return 모의고사 점수
     */
    public MockExamScoresResponse getMockExamScores(User user, Long evaluationId) {
        FinalGrade finalGrade = finalGradeRepository.findByUserIdAndEvaluationId(user.getId(), evaluationId);

        if (finalGrade == null) {
            throw new EntityNotFoundException(ErrorCode.EVALUATION_NOT_FOUND);
        }

        return MockExamScoresResponse.from(finalGrade);
    }
}

