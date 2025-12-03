package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions_step3")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionStep3 extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Column(name = "situation_description", columnDefinition = "TEXT")
    private String situationDescription;

    @Column(name = "aircraft_type", length = 20)
    private String aircraftType;

    @Column(name = "departure", length = 10)
    private String departure;

    @Column(name = "arrival", length = 10)
    private String arrival;

    @Column(name = "boarding_date")
    private String boardingDate;

    @Column(name = "script_korean", columnDefinition = "TEXT")
    private String scriptKorean;

    @Column(name = "script_english", columnDefinition = "TEXT")
    private String scriptEnglish;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "is_used", length = 10)
    private String isUsed;

    public QuestionStep3(String situationDescription, String aircraftType, String departure, String arrival, String boardingDate, String scriptKorean, String scriptEnglish, String explanation) {
        this.situationDescription = situationDescription;
        this.aircraftType = aircraftType;
        this.departure = departure;
        this.arrival = arrival;
        this.boardingDate = boardingDate;
        this.scriptKorean = scriptKorean;
        this.scriptEnglish = scriptEnglish;
        this.explanation = explanation;
        this.isUsed = "MOCK";
    }

    public void update(String situationDescription, String aircraftType, String departure, String arrival, String boardingDate, String scriptKorean, String scriptEnglish, String explanation) {
        this.situationDescription = situationDescription;
        this.aircraftType = aircraftType;
        this.departure = departure;
        this.arrival = arrival;
        this.boardingDate = boardingDate;
        this.scriptKorean = scriptKorean;
        this.scriptEnglish = scriptEnglish;
        this.explanation = explanation;
    }
}
