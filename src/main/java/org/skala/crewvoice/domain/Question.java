package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Column(name = "situation_description", nullable = false, columnDefinition = "TEXT")
    private String situationDescription;

    @Column(name = "aircraft_type", nullable = false)
    private String aircraftType;

    @Column(name = "explaination", columnDefinition = "TEXT")
    private String explaination;

    @Column(name = "step", nullable = false)
    private int step;

    public Question(String situationDescription, String aircraftType, String explaination, int step) {
        this.situationDescription = situationDescription;
        this.aircraftType = aircraftType;
        this.explaination = explaination;
        this.step = step;
    }

    public void update(String situationDescription, String aircraftType, String explaination) {
        this.situationDescription = situationDescription;
        this.aircraftType = aircraftType;
        this.explaination = explaination;
    }
}
