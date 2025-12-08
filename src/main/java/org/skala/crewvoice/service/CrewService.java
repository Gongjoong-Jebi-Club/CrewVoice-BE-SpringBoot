package org.skala.crewvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.domain.Evaluation;
import org.skala.crewvoice.domain.EvaluationTargetUser;
import org.skala.crewvoice.domain.FinalGrade;
import org.skala.crewvoice.domain.User;
import org.skala.crewvoice.dto.response.CrewInfoResponse;
import org.skala.crewvoice.dto.response.CrewListResponse;
import org.skala.crewvoice.dto.response.CrewManagementResponse;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.repository.EvaluationRepository;
import org.skala.crewvoice.repository.EvaluationTargetUserRepository;
import org.skala.crewvoice.repository.FinalGradeRepository;
import org.skala.crewvoice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    private final EvaluationTargetUserRepository evaluationTargetUserRepository;
    private final FinalGradeRepository finalGradeRepository;

    /**
     * 특정 평가의 모든 대상자 정보 조회, 검색 및 필터링
     *
     * @param evaluationId 평가 ID
     * @param keyword 검색 키워드 (이름 또는 사번, optional)
     * @param completeStatus 완료 상태 필터 (all, completed, incomplete, optional)
     * @return 평가 대상자 목록
     */
    public CrewListResponse getCrewInfoByEvaluation(Long evaluationId, String keyword, String completeStatus) {
        log.info("평가 대상자 정보 조회 시작 - 평가ID: {}, 검색 키워드: {}, 완료 상태 필터: {}",
                evaluationId, keyword, completeStatus);

        // 1. 평가 존재 여부 확인
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> {
                    log.error("평가를 찾을 수 없습니다 - 평가ID: {}", evaluationId);
                    return new EntityNotFoundException(ErrorCode.EVALUATION_NOT_FOUND);
                });

        // 2. evaluation_target_users 테이블에서 해당 평가의 대상자 userId 목록 조회
        List<EvaluationTargetUser> targetUsers = evaluationTargetUserRepository
                .findByEvaluationEvaluationId(evaluationId);

        log.info("평가 대상자 수: {}", targetUsers.size());

        // 3. 대상자들의 User 정보 조회 (userId 리스트로 변환)
        List<Long> targetUserIds = targetUsers.stream()
                .map(targetUser -> targetUser.getUser().getId())
                .collect(Collectors.toList());

        // 4. Users 테이블에서 대상자 정보 조회
        List<User> targetCrewMembers = targetUserIds.stream()
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND)))
                .collect(Collectors.toList());

        // 5. 키워드로 필터링 (이름 또는 사번 검색)
        List<User> filteredCrewMembers = targetCrewMembers;
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.trim().toLowerCase();
            filteredCrewMembers = targetCrewMembers.stream()
                    .filter(user -> user.getName().toLowerCase().contains(lowerKeyword)
                                 || user.getEmployeeId().toLowerCase().contains(lowerKeyword))
                    .collect(Collectors.toList());
            log.info("키워드 검색 후 대상자 수: {} (키워드: {})", filteredCrewMembers.size(), keyword);
        }

        // 6. 각 사용자별로 해당 평가 성적 조회 및 Response DTO 생성
        List<CrewInfoResponse> crewInfoList = filteredCrewMembers.stream()
                .map(user -> {
                    // 해당 사용자의 해당 평가 성적 조회 (없을 수 있음)
                    FinalGrade finalGrade = finalGradeRepository
                            .findByUserIdAndEvaluationId(user.getId(), evaluationId);

                    return CrewInfoResponse.of(user, finalGrade);
                })
                .toList();

        // 7. 완료 상태로 필터링
        List<CrewInfoResponse> filteredList = filterByCompletionStatus(crewInfoList, completeStatus);

        log.info("평가 대상자 정보 조회 완료 - 총 {}명 (완료: {}명, 미완료: {}명)",
                filteredList.size(),
                filteredList.stream().filter(CrewInfoResponse::getIsCompleted).count(),
                filteredList.stream().filter(crew -> !crew.getIsCompleted()).count());

        return CrewListResponse.of(filteredList);
    }

    /**
     * 완료 상태로 필터링 (영어/한글 모두 지원)
     *
     * @param crewInfoList 전체 대상자 목록
     * @param completeStatus 완료 상태 필터 (completed/완료, incompleted/미완료)
     * @return 필터링된 대상자 목록
     */
    private List<CrewInfoResponse> filterByCompletionStatus(List<CrewInfoResponse> crewInfoList, String completeStatus) {
        log.info("완료 상태 필터링 시작 - completeStatus: '{}', 전체 대상자 수: {}", completeStatus, crewInfoList.size());

        // completeStatus가 없거나 빈 문자열이면 전체 반환
        if (completeStatus == null || completeStatus.trim().isEmpty()) {
            log.info("완료 상태 필터 없음 - 전체 반환");
            return crewInfoList;
        }

        // "completed" 또는 "완료" - 완료한 대상자만 (isCompleted == true)
        if (completeStatus.equalsIgnoreCase("completed") || completeStatus.equals("완료")) {
            log.info("완료 상태 필터: 완료만 (completeStatus: {})", completeStatus);
            List<CrewInfoResponse> filtered = crewInfoList.stream()
                    .filter(CrewInfoResponse::getIsCompleted)
                    .toList();
            log.info("필터링 결과: {}명 (완료자만)", filtered.size());
            return filtered;
        }

        // "incompleted" 또는 "미완료" - 미완료 대상자만 (isCompleted == false)
        if (completeStatus.equalsIgnoreCase("incompleted") || completeStatus.equals("미완료")) {
            log.info("완료 상태 필터: 미완료만 (completeStatus: {})", completeStatus);
            List<CrewInfoResponse> filtered = crewInfoList.stream()
                    .filter(crew -> !crew.getIsCompleted())
                    .toList();
            log.info("필터링 결과: {}명 (미완료자만)", filtered.size());
            return filtered;
        }

        // 잘못된 completeStatus 값이면 전체 반환 (에러 대신 관대하게 처리)
        log.warn("잘못된 완료 상태 필터 값: '{}' - 전체 반환", completeStatus);
        return crewInfoList;
    }

    /**
     * 전체 피평가자(CREW)의 정보와 최신 등급 조회 또는 검색
     *
     * <p>관리자가 "피평가자 관리" 페이지에서 모든 CREW의 정보를 조회하거나 검색합니다.
     * 각 피평가자의 가장 최신 EXAM 타입 FinalGrade를 조회하여 한국어/영어 등급을 함께 반환합니다.
     *
     * @param keyword 검색 키워드 (이름 또는 사번, optional)
     * @return 전체 또는 검색된 피평가자 목록 (최신 EXAM 등급 포함)
     */
    public List<CrewManagementResponse> getAllCrewsWithLatestGrades(String keyword) {
        log.info("전체 피평가자 정보 조회 시작 - 검색 키워드: {}", keyword);

        // 1. 키워드 여부에 따라 적절한 Repository 메서드 호출
        List<User> allCrews;
        if (keyword == null || keyword.trim().isEmpty()) {
            // 키워드 없으면 전체 CREW 조회
            allCrews = userRepository.findAllCrews();
            log.info("전체 CREW 사용자 수: {}", allCrews.size());
        } else {
            // 키워드 있으면 이름 또는 사번으로 검색 (이메일 제외)
            allCrews = userRepository.searchCrewsByKeyword(keyword.trim());
            log.info("검색된 CREW 사용자 수: {} (키워드: {})", allCrews.size(), keyword);
        }

        // 2. 각 CREW의 최신 EXAM 타입 FinalGrade 조회 및 Response DTO 생성
        List<CrewManagementResponse> responses = allCrews.stream()
                .map(user -> {
                    // 해당 사용자의 가장 최신 EXAM 타입 최종 성적 조회 (없을 수 있음)
                    FinalGrade latestGrade = finalGradeRepository
                            .findLatestByUserId(user.getId())
                            .orElse(null);

                    return CrewManagementResponse.from(user, latestGrade);
                })
                .toList();

        log.info("피평가자 정보 조회 완료 - 총 {}명 (등급 보유: {}명)",
                responses.size(),
                responses.stream().filter(r -> !"등급없음".equals(r.getKorGrade())).count());

        return responses;
    }
}