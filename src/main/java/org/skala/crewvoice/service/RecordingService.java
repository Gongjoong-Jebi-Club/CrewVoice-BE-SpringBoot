// src/main/java/org/skala/crewvoice/service/RecordingService.java
package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.domain.Evaluation;
import org.skala.crewvoice.domain.Language;
import org.skala.crewvoice.domain.SubmissionStep2;
import org.skala.crewvoice.domain.SubmissionStep3;
import org.skala.crewvoice.domain.User;
import org.skala.crewvoice.dto.request.RecordingDownloadRequest;
import org.skala.crewvoice.dto.response.RecordingDownloadResponse;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.exception.custom.InvalidRequestException;
import org.skala.crewvoice.repository.EvaluationRepository;
import org.skala.crewvoice.repository.SubmissionStep2Repository;
import org.skala.crewvoice.repository.SubmissionStep3Repository;
import org.skala.crewvoice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 녹음 파일 다운로드 관련 비즈니스 로직
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecordingService {

    private final SubmissionStep2Repository submissionStep2Repository;
    private final SubmissionStep3Repository submissionStep3Repository;
    private final UserRepository userRepository;
    private final EvaluationRepository evaluationRepository;
    private final S3Service s3Service;

    private static final int PRESIGNED_URL_EXPIRATION_SECONDS = 900; // 15분

    /**
     * 녹음 파일 다운로드 URL 생성
     *
     * 트랜잭션 최적화: DB 조회만 트랜잭션 내에서 수행하고, S3 호출은 트랜잭션 밖에서 수행
     *
     * @param request 다운로드 요청 정보
     * @return 다운로드 URL 정보
     */
    public RecordingDownloadResponse getRecordingDownloadUrl(RecordingDownloadRequest request) {
        log.info("녹음 파일 다운로드 URL 생성 시작 - evaluationId: {}, userId: {}, step: {}, question: {}, language: {}",
                request.getEvaluationId(), request.getUserId(), request.getStep(),
                request.getQuestion(), request.getLanguage());

        // 1. 파라미터 검증
        validateParameters(request);

        // 2. DB 조회 (트랜잭션 내에서)
        RecordingInfo recordingInfo = fetchRecordingInfo(request);

        // 3. S3 파일 존재 확인 및 Pre-signed URL 생성 (트랜잭션 밖에서)
        if (!s3Service.doesObjectExist(recordingInfo.getObjectKey())) {
            log.error("S3 파일이 존재하지 않음 - Key: {}", recordingInfo.getObjectKey());
            throw new EntityNotFoundException(ErrorCode.RECORDING_FILE_NOT_FOUND);
        }

        String presignedUrl = s3Service.generatePresignedUrl(
                recordingInfo.getObjectKey(),
                PRESIGNED_URL_EXPIRATION_SECONDS
        );

        // 4. 파일명 생성
        String fileName = generateFileName(recordingInfo.getUser(), request);

        // 5. Response 생성
        RecordingDownloadResponse response = RecordingDownloadResponse.builder()
                .evaluationId(request.getEvaluationId())
                .userId(request.getUserId())
                .userName(recordingInfo.getUser().getName())
                .employeeId(recordingInfo.getUser().getEmployeeId())
                .step(request.getStep())
                .question(request.getQuestion())
                .language(request.getLanguage())
                .fileUrl(presignedUrl)
                .expiresIn(PRESIGNED_URL_EXPIRATION_SECONDS)
                .fileName(fileName)
                .build();

        log.info("녹음 파일 다운로드 URL 생성 완료 - userId: {}, fileName: {}", request.getUserId(), fileName);

        return response;
    }

    /**
     * 파라미터 검증
     */
    private void validateParameters(RecordingDownloadRequest request) {
        // STEP2는 question 불필요
        if (request.getStep() == 2 && request.getQuestion() != null) {
            throw new InvalidRequestException(ErrorCode.STEP2_NO_QUESTION_NEEDED);
        }

        // STEP3는 question 필수
        if (request.getStep() == 3 && request.getQuestion() == null) {
            throw new InvalidRequestException(ErrorCode.STEP3_QUESTION_REQUIRED);
        }
    }

    /**
     * DB에서 필요한 정보 조회 (트랜잭션 내)
     */
    @Transactional(readOnly = true)
    protected RecordingInfo fetchRecordingInfo(RecordingDownloadRequest request) {
        // 1. 사용자 정보 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 2. 평가 정보 조회 (추가됨)
        Evaluation evaluation = evaluationRepository.findById(request.getEvaluationId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EVALUATION_NOT_FOUND));

        // 3. S3 Object Key 추출
        String objectKey = getObjectKey(request);

        return new RecordingInfo(user, evaluation, objectKey);
    }

    /**
     * S3 Object Key 추출
     */
    private String getObjectKey(RecordingDownloadRequest request) {
        if (request.getStep() == 2) {
            return getStep2ObjectKey(request);
        } else {
            return getStep3ObjectKey(request);
        }
    }

    /**
     * STEP2 Object Key 추출
     */
    private String getStep2ObjectKey(RecordingDownloadRequest request) {
        List<SubmissionStep2> submissions = submissionStep2Repository
                .findByUserIdAndEvaluationId(request.getUserId(), request.getEvaluationId());

        if (submissions.isEmpty()) {
            throw new EntityNotFoundException(ErrorCode.SUBMISSION_NOT_FOUND);
        }

        SubmissionStep2 submission = submissions.get(0);

        // Language Enum의 selectUrl 메서드 활용
        String objectKey = request.getLanguage().selectUrl(
                submission.getAudioFileKoreanUrl(),
                submission.getAudioFileEnglishUrl()
        );

        if (objectKey == null || objectKey.trim().isEmpty()) {
            log.error("음성 파일이 업로드되지 않음 - userId: {}, evaluationId: {}, language: {}",
                    request.getUserId(), request.getEvaluationId(), request.getLanguage());
            throw new InvalidRequestException(ErrorCode.RECORDING_FILE_NOT_UPLOADED);
        }

        return objectKey;
    }

    /**
     * STEP3 Object Key 추출
     */
    private String getStep3ObjectKey(RecordingDownloadRequest request) {
        SubmissionStep3 submission = submissionStep3Repository
                .findByUserIdAndEvaluationIdAndPartOrder(
                        request.getUserId(),
                        request.getEvaluationId(),
                        request.getQuestion()
                )
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SUBMISSION_NOT_FOUND));

        // Language Enum의 selectUrl 메서드 활용
        String objectKey = request.getLanguage().selectUrl(
                submission.getAudioFileKoreanUrl(),
                submission.getAudioFileEnglishUrl()
        );

        if (objectKey == null || objectKey.trim().isEmpty()) {
            log.error("음성 파일이 업로드되지 않음 - userId: {}, evaluationId: {}, question: {}, language: {}",
                    request.getUserId(), request.getEvaluationId(), request.getQuestion(), request.getLanguage());
            throw new InvalidRequestException(ErrorCode.RECORDING_FILE_NOT_UPLOADED);
        }

        return objectKey;
    }

    /**
     * 다운로드 파일명 생성
     * 예: "EMP006_김서윤_STEP2_한국어.wav"
     */
    private String generateFileName(User user, RecordingDownloadRequest request) {
        String stepStr = request.getStep() == 2
                ? "STEP2"
                : String.format("STEP3_%d", request.getQuestion());

        return String.format("%s_%s_%s_%s.wav",
                user.getEmployeeId(),
                user.getName(),
                stepStr,
                request.getLanguage().getDisplayName());
    }

    /**
     * DB 조회 결과를 담는 내부 클래스
     */
    @lombok.Value
    private static class RecordingInfo {
        User user;
        Evaluation evaluation;
        String objectKey;
    }
}
