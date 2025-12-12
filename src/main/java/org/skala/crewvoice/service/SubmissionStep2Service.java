package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.domain.*;
import org.skala.crewvoice.dto.request.SubmissionStep2Request;
import org.skala.crewvoice.dto.response.SubmissionStep2Response;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.repository.EvaluationRepository;
import org.skala.crewvoice.repository.QuestionStep2Repository;
import org.skala.crewvoice.repository.SubmissionStep2Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * STEP2 제출 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionStep2Service {

    private final SubmissionStep2Repository submissionStep2Repository;
    private final EvaluationRepository evaluationRepository;
    private final QuestionStep2Repository questionStep2Repository;
    private final S3Service s3Service;
    private final EvaluationService evaluationService;

    /**
     * STEP2 음성 파일 제출
     *
     * @param user 인증된 사용자
     * @param audioFile 음성 파일
     * @param request 제출 메타데이터
     * @return 제출 응답 정보
     */
    @Transactional
    public SubmissionStep2Response submitAudio(User user, MultipartFile audioFile, SubmissionStep2Request request) {
        log.info("STEP2 음성 파일 제출 시작 - userId: {}, evaluationId: {}, questionId: {}, language: {}",
                user.getId(), request.getEvaluationId(), request.getQuestionId(), request.getLanguage());

        // 1. Evaluation 존재 확인
        Evaluation evaluation = evaluationRepository.findById(request.getEvaluationId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EVALUATION_NOT_FOUND));

        // 2. Question 존재 확인
        QuestionStep2 question = questionStep2Repository.findById(request.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.QUESTION_NOT_FOUND));

        // 3. String 언어 코드를 Language Enum으로 변환
        Language language = Language.fromCode(request.getLanguage());

        // 4. S3에 음성 파일 업로드 (Object Key 저장)
        String objectKey = s3Service.uploadFileForStep2(audioFile, request.getEvaluationId(),
                user.getId(), request.getQuestionId(), request.getLanguage());
        log.info("S3 업로드 완료 - Object Key: {}", objectKey);

        // 5. SubmissionStep2 엔티티 생성 및 저장
        SubmissionStep2 submission = SubmissionStep2.builder()
                .user(user)
                .evaluation(evaluation)
                .question(question)
                .language(language)
                .audioFileUrl(objectKey)  // Object Key를 저장
                .build();

        SubmissionStep2 savedSubmission = submissionStep2Repository.save(submission);

        log.info("STEP2 제출 완료 - submissionId: {}", savedSubmission.getSubmissionId());

        // 6. 자동으로 FastAPI 평가 시작 (비동기)
        try {
            evaluationService.startEvaluationAsync(savedSubmission.getSubmissionId(), 2);
            log.info("FastAPI 평가 요청 자동 전송 완료 - submissionId: {}", savedSubmission.getSubmissionId());
        } catch (Exception e) {
            log.error("FastAPI 평가 요청 실패 - submissionId: {}, error: {}",
                    savedSubmission.getSubmissionId(), e.getMessage(), e);
            // 평가 시작 실패해도 제출은 성공으로 처리 (백그라운드 작업이므로)
        }

        // 7. Response 생성
        return SubmissionStep2Response.of(
                savedSubmission.getSubmissionId(),
                savedSubmission.getAudioFileUrl(),
                audioFile.getSize(),
                savedSubmission.getSubmittedAt()
        );
    }
}
