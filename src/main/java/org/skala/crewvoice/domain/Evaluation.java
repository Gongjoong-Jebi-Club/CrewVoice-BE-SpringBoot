package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.InvalidRequestException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 평가(시험) 엔티티
 *
 * <p>승무원들이 응시할 기내방송 평가 시험 정보를 관리합니다.
 * 6가지 평가 항목(발음, 억양, 강세, 쉼, 톤, 속도)의 가중치를 설정할 수 있습니다.
 */
@Entity
@Table(name = "evaluations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Evaluation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id", nullable = false)
    private Long evaluationId;

    @Column(name = "evaluation_name", nullable = false, length = 100)
    private String evaluationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_type", nullable = false, length = 10)
    private EvaluationType evaluationType;

    @Column(name = "start_date", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime startDate;

    @Column(name = "end_date", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime endDate;

    @Column(name = "weight_pronunciation", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightPronunciation;

    @Column(name = "weight_pause", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightPause;

    @Column(name = "weight_intonation", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightIntonation;

    @Column(name = "weight_tone", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightTone;

    @Column(name = "weight_speed", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightSpeed;

    @Column(name = "weight_accent", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightAccent;

    // ===== FastAPI 파라미터 가중치 =====
    // Tone (4개)
    @Column(name = "weight_clarity", nullable = false, precision = 4, scale = 3)
    private BigDecimal weightClarity;

    @Column(name = "weight_stability", nullable = false, precision = 4, scale = 3)
    private BigDecimal weightStability;

    @Column(name = "weight_brightness", nullable = false, precision = 4, scale = 3)
    private BigDecimal weightBrightness;

    @Column(name = "weight_pitch_position", nullable = false, precision = 4, scale = 3)
    private BigDecimal weightPitchPosition;

    // Pause (3개)
    @Column(name = "weight_fluency", nullable = false, precision = 4, scale = 3)
    private BigDecimal weightFluency;

    @Column(name = "weight_position", nullable = false, precision = 4, scale = 3)
    private BigDecimal weightPosition;

    @Column(name = "weight_length", nullable = false, precision = 4, scale = 3)
    private BigDecimal weightLength;

    // Accent (2개)
    @Column(name = "weight_pitch", nullable = false, precision = 4, scale = 3)
    private BigDecimal weightPitch;

    @Column(name = "weight_energy", nullable = false, precision = 4, scale = 3)
    private BigDecimal weightEnergy;

    // Intonation (2개)
    @Column(name = "weight_baseline", nullable = false, precision = 4, scale = 3)
    private BigDecimal weightBaseline;

    @Column(name = "weight_iso", nullable = false, precision = 4, scale = 3)
    private BigDecimal weightIso;

    // Speed (1개)
    @Column(name = "deviation_threshold", nullable = false, precision = 3, scale = 1)
    private BigDecimal deviationThreshold;

    @OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluationTargetUser> targetUsers = new ArrayList<>();

    @Builder
    public Evaluation(String evaluationName, EvaluationType evaluationType, OffsetDateTime startDate,
                      OffsetDateTime endDate, BigDecimal weightPronunciation, BigDecimal weightPause,
                      BigDecimal weightIntonation, BigDecimal weightTone, BigDecimal weightSpeed,
                      BigDecimal weightAccent,
                      // FastAPI 파라미터
                      BigDecimal weightClarity, BigDecimal weightStability, BigDecimal weightBrightness,
                      BigDecimal weightPitchPosition, BigDecimal weightFluency, BigDecimal weightPosition,
                      BigDecimal weightLength, BigDecimal weightPitch, BigDecimal weightEnergy,
                      BigDecimal weightBaseline, BigDecimal weightIso, BigDecimal deviationThreshold) {
        this.evaluationName = evaluationName;
        this.evaluationType = evaluationType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.weightPronunciation = weightPronunciation;
        this.weightPause = weightPause;
        this.weightIntonation = weightIntonation;
        this.weightTone = weightTone;
        this.weightSpeed = weightSpeed;
        this.weightAccent = weightAccent;
        this.weightClarity = weightClarity;
        this.weightStability = weightStability;
        this.weightBrightness = weightBrightness;
        this.weightPitchPosition = weightPitchPosition;
        this.weightFluency = weightFluency;
        this.weightPosition = weightPosition;
        this.weightLength = weightLength;
        this.weightPitch = weightPitch;
        this.weightEnergy = weightEnergy;
        this.weightBaseline = weightBaseline;
        this.weightIso = weightIso;
        this.deviationThreshold = deviationThreshold;
    }

    /**
     * 가중치 합계 검증 (총합 100 확인)
     */
    public void validateWeights() {
        BigDecimal total = weightPronunciation
                .add(weightPause)
                .add(weightIntonation)
                .add(weightTone)
                .add(weightSpeed)
                .add(weightAccent);

        if (total.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new InvalidRequestException(
                    "가중치 합계는 100이어야 합니다. 현재 합계: " + total,
                    ErrorCode.INVALID_INPUT_VALUE
            );
        }
    }

    /**
     * 날짜 범위 검증
     */
    public void validateDateRange() {
        if (startDate.isAfter(endDate)) {
            throw new InvalidRequestException(
                    "시작 날짜는 종료 날짜보다 이전이어야 합니다.",
                    ErrorCode.INVALID_INPUT_VALUE
            );
        }
    }

    /**
     * 모든 FastAPI 파라미터 가중치 검증
     */
    public void validateAllFastAPIWeights() {
        validateToneWeights();
        validatePauseWeights();
        validateAccentWeights();
        validateIntonationWeights();
        validateDeviationThreshold();
    }

    private void validateToneWeights() {
        BigDecimal total = weightClarity.add(weightStability)
                .add(weightBrightness).add(weightPitchPosition);
        if (total.compareTo(BigDecimal.ONE) != 0) {
            throw new InvalidRequestException(
                    "Tone 가중치 합계는 1.0이어야 합니다. 현재 합계: " + total,
                    ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private void validatePauseWeights() {
        BigDecimal total = weightFluency.add(weightPosition).add(weightLength);
        if (total.compareTo(BigDecimal.ONE) != 0) {
            throw new InvalidRequestException(
                    "Pause 가중치 합계는 1.0이어야 합니다. 현재 합계: " + total,
                    ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private void validateAccentWeights() {
        BigDecimal total = weightPitch.add(weightEnergy);
        if (total.compareTo(BigDecimal.ONE) != 0) {
            throw new InvalidRequestException(
                    "Accent 가중치 합계는 1.0이어야 합니다. 현재 합계: " + total,
                    ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private void validateIntonationWeights() {
        BigDecimal total = weightBaseline.add(weightIso);
        if (total.compareTo(BigDecimal.ONE) != 0) {
            throw new InvalidRequestException(
                    "Intonation 가중치 합계는 1.0이어야 합니다. 현재 합계: " + total,
                    ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private void validateDeviationThreshold() {
        if (deviationThreshold.compareTo(BigDecimal.valueOf(5.0)) < 0
                || deviationThreshold.compareTo(BigDecimal.valueOf(10.0)) > 0) {
            throw new InvalidRequestException(
                    "Deviation Threshold는 5.0~10.0 사이여야 합니다. 현재 값: " + deviationThreshold,
                    ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    // ===== 비즈니스 로직 메서드 =====

    /**
     * 평가가 현재 진행 중인지 확인
     *
     * @return 진행 중이면 true
     */
    public boolean isActive() {
        OffsetDateTime now = OffsetDateTime.now();
        return now.isAfter(startDate) && now.isBefore(endDate);
    }

    /**
     * 평가가 아직 시작하지 않았는지 확인
     *
     * @return 시작 전이면 true
     */
    public boolean isUpcoming() {
        return OffsetDateTime.now().isBefore(startDate);
    }

    /**
     * 평가가 종료되었는지 확인
     *
     * @return 종료되었으면 true
     */
    public boolean isCompleted() {
        return OffsetDateTime.now().isAfter(endDate);
    }

    /**
     * 제출을 받을 수 있는 상태인지 확인
     *
     * @return 제출 가능하면 true
     */
    public boolean canAcceptSubmission() {
        return isActive();
    }

    /**
     * 실전 평가인지 확인
     *
     * @return 실전 평가면 true
     */
    public boolean isExam() {
        return this.evaluationType == EvaluationType.EXAM;
    }

    /**
     * 모의 평가인지 확인
     *
     * @return 모의 평가면 true
     */
    public boolean isMock() {
        return this.evaluationType == EvaluationType.MOCK;
    }

    /**
     * 가중치 총합 계산
     *
     * @return 가중치 총합
     */
    public BigDecimal getTotalWeight() {
        return weightPronunciation
                .add(weightPause)
                .add(weightIntonation)
                .add(weightTone)
                .add(weightSpeed)
                .add(weightAccent);
    }

    /**
     * 평가 항목별 가중치 맵 반환
     *
     * @return 항목명: 가중치 맵
     */
    public Map<String, BigDecimal> getWeightMap() {
        return Map.of(
                "pronunciation", weightPronunciation,
                "pause", weightPause,
                "intonation", weightIntonation,
                "tone", weightTone,
                "speed", weightSpeed,
                "accent", weightAccent
        );
    }

    /**
     * 평가 기간이 유효한지 확인 (현재 시각 기준)
     *
     * @return 유효한 기간이면 true
     */
    public boolean hasValidPeriod() {
        return startDate.isBefore(endDate);
    }

    /**
     * 남은 기간(일) 계산
     *
     * @return 남은 일수 (음수면 이미 종료)
     */
    public long getRemainingDays() {
        OffsetDateTime now = OffsetDateTime.now();
        if (now.isAfter(endDate)) {
            return -1;
        }
        return java.time.Duration.between(now, endDate).toDays();
    }
}