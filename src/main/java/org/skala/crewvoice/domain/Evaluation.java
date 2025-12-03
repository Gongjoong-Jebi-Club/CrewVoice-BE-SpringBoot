package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Evaluation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id")
    private Long id;

    @Column(name = "evaluation_name", length = 100, nullable = false)
    private String evaluationName;

    @Column(name = "evaluation_type", length = 10, nullable = false)
    private String evaluationType;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "weight_pronunciation", precision = 5, scale = 2, nullable = false)
    private BigDecimal weightPronunciation;

    @Column(name = "weight_pause", precision = 5, scale = 2, nullable = false)
    private BigDecimal weightPause;

    @Column(name = "weight_intonation", precision = 5, scale = 2, nullable = false)
    private BigDecimal weightIntonation;

    @Column(name = "weight_tone", precision = 5, scale = 2, nullable = false)
    private BigDecimal weightTone;

    @Column(name = "weight_speed", precision = 5, scale = 2, nullable = false)
    private BigDecimal weightSpeed;

    @Column(name = "weight_accent", precision = 5, scale = 2, nullable = false)
    private BigDecimal weightAccent;

    public Evaluation(String evaluationName, String evaluationType, LocalDateTime startDate, LocalDateTime endDate,
                      BigDecimal weightPronunciation, BigDecimal weightPause, BigDecimal weightIntonation,
                      BigDecimal weightTone, BigDecimal weightSpeed, BigDecimal weightAccent) {
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
}
