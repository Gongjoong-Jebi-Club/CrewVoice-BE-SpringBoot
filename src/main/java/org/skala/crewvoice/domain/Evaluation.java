package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.InvalidRequestException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

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

    @Builder
    public Evaluation(String evaluationName, EvaluationType evaluationType, OffsetDateTime startDate,
                      OffsetDateTime endDate, BigDecimal weightPronunciation, BigDecimal weightPause,
                      BigDecimal weightIntonation, BigDecimal weightTone, BigDecimal weightSpeed,
                      BigDecimal weightAccent) {
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
}