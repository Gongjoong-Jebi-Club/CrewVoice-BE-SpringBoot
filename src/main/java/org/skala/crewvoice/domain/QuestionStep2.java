package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions_step2")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionStep2 extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Column(name = "situation_description", columnDefinition = "TEXT")
    private String situationDescription;

    @Column(name = "aircraft_type", length = 20)
    private String aircraftType;

    @Column(name = "script_korean", columnDefinition = "TEXT")
    private String scriptKorean;

    @Column(name = "script_english", columnDefinition = "TEXT")
    private String scriptEnglish;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "is_used", length = 10)
    private String isUsed;

    public QuestionStep2(String situationDescription, String aircraftType, String scriptKorean, String scriptEnglish, String explanation) {
        this.situationDescription = situationDescription;
        this.aircraftType = aircraftType;
        this.scriptKorean = scriptKorean;
        this.scriptEnglish = scriptEnglish;
        this.explanation = explanation;
        this.isUsed = "MOCK";
    }

    public void update(String situationDescription, String aircraftType, String explanation) {
        this.situationDescription = situationDescription;
        this.aircraftType = aircraftType;
        this.explanation = explanation;
    }
}
