package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.domain.*;
import org.skala.crewvoice.dto.request.SubmissionStep3Request;
import org.skala.crewvoice.dto.response.SubmissionStep3Response;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.repository.EvaluationRepository;
import org.skala.crewvoice.repository.QuestionStep3Repository;
import org.skala.crewvoice.repository.SubmissionStep3Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * STEP3 제출 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionStep3Service {

    private final SubmissionStep3Repository submissionStep3Repository;
    private final EvaluationRepository evaluationRepository;
    private final QuestionStep3Repository questionStep3Repository;
    private final S3Service s3Service;
    private final EvaluationService evaluationService;

    /**
     * STEP3 음성 파일 제출
     *
     * @param user 인증된 사용자
     * @param audioFile 음성 파일
     * @param request 제출 메타데이터
     * @return 제출 응답 정보
     */
    @Transactional
    public SubmissionStep3Response submitAudio(User user, MultipartFile audioFile, SubmissionStep3Request request) {
        log.info("STEP3 음성 파일 제출 시작 - userId: {}, evaluationId: {}, questionId: {}, partOrder: {}, language: {}",
                user.getId(), request.getEvaluationId(), request.getQuestionId(),
                request.getPartOrder(), request.getLanguage());

        // 1. Evaluation 존재 확인
        Evaluation evaluation = evaluationRepository.findById(request.getEvaluationId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EVALUATION_NOT_FOUND));

        // 2. Question 존재 확인
        QuestionStep3 question = questionStep3Repository.findById(request.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.QUESTION_NOT_FOUND));

        // 3. String 언어 코드를 Language Enum으로 변환
        Language language = Language.fromCode(request.getLanguage());

        // 4. S3에 음성 파일 업로드 (Object Key 저장)
        String objectKey = s3Service.uploadFileForStep3(audioFile, request.getEvaluationId(),
                user.getId(), request.getQuestionId(), request.getPartOrder(), request.getLanguage());
        log.info("S3 업로드 완료 - Object Key: {}", objectKey);

        // 5. SubmissionStep3 엔티티 생성 및 저장
        SubmissionStep3 submission = SubmissionStep3.builder()
                .user(user)
                .evaluation(evaluation)
                .question(question)
                .partOrder(request.getPartOrder())
                .language(language)
                .audioFileUrl(objectKey)  // Object Key를 저장
                .build();

        SubmissionStep3 savedSubmission = submissionStep3Repository.save(submission);

        log.info("STEP3 제출 완료 - submissionId: {}", savedSubmission.getSubmissionId());

        // 6. 자동으로 FastAPI 평가 시작 (비동기)
        try {
            evaluationService.startEvaluationAsync(savedSubmission.getSubmissionId(), 3);
            log.info("FastAPI 평가 요청 자동 전송 완료 - submissionId: {}", savedSubmission.getSubmissionId());
        } catch (Exception e) {
            log.error("FastAPI 평가 요청 실패 - submissionId: {}, error: {}",
                    savedSubmission.getSubmissionId(), e.getMessage(), e);
            // 평가 시작 실패해도 제출은 성공으로 처리 (백그라운드 작업이므로)
        }

        // 7. Response 생성
        return SubmissionStep3Response.of(
                savedSubmission.getSubmissionId(),
                savedSubmission.getAudioFileUrl(),
                audioFile.getSize(),
                savedSubmission.getSubmittedAt()
        );
    }
}
