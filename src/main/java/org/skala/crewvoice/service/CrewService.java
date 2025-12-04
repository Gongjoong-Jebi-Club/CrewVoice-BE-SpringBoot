package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.domain.Evaluation;
import org.skala.crewvoice.domain.FinalGrade;
import org.skala.crewvoice.domain.User;
import org.skala.crewvoice.dto.response.CrewInfoResponse;
import org.skala.crewvoice.dto.response.CrewListResponse;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.repository.EvaluationRepository;
import org.skala.crewvoice.repository.FinalGradeRepository;
import org.skala.crewvoice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 평가 대상자(Crew) 관련 비즈니스 로직 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewService {

    private final UserRepository userRepository;
    private final EvaluationRepository evaluationRepository;
    private final FinalGradeRepository finalGradeRepository;

    /**
     * 특정 평가의 모든 대상자 정보 조회
     *
     * @param evaluationId 평가 ID
     * @return 평가 대상자 목록
     */
    public CrewListResponse getCrewInfoByEvaluation(Long evaluationId) {
        log.info("평가 대상자 정보 조회 시작 - 평가ID: {}", evaluationId);

        // 1. 평가 존재 여부 확인
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> {
                    log.error("평가를 찾을 수 없습니다 - 평가ID: {}", evaluationId);
                    return new EntityNotFoundException(ErrorCode.EVALUATION_NOT_FOUND);
                });

        // 2. 모든 ACTIVE CREW 사용자 조회
        List<User> allCrewMembers = userRepository.findAllActiveCrewMembers();
        log.info("전체 CREW 대상자 수: {}", allCrewMembers.size());

        // 3. 각 사용자별로 성적 조회 및 Response DTO 생성
        List<CrewInfoResponse> crewInfoList = allCrewMembers.stream()
                .map(user -> {
                    // 해당 사용자의 해당 평가 성적 조회 (없을 수 있음)
                    FinalGrade finalGrade = finalGradeRepository
                            .findByUserIdAndEvaluationId(user.getId(), evaluationId);

                    return CrewInfoResponse.of(user, finalGrade);
                })
                .toList();

        log.info("평가 대상자 정보 조회 완료 - 총 {}명 (완료: {}명)",
                crewInfoList.size(),
                crewInfoList.stream().filter(CrewInfoResponse::getState).count());

        return CrewListResponse.of(crewInfoList);
    }
}