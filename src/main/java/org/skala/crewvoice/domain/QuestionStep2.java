package org.skala.crewvoice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.skala.crewvoice.domain.BaseTimeEntity;

/**
 * STEP2 문제 엔티티 (스크립트 읽기)
 */
@Entity
@Table(name = "questions_step2")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class QuestionStep2 extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("문제 ID")
    @Column(name = "question_id")
    private Long questionId;

    @Comment("상황 설명")
    @Column(name = "situation_description", columnDefinition = "TEXT")
    private String situationDescription;

    @Comment("기종 (A380, B777 등)")
    @Column(name = "aircraft_type", length = 20)
    private String aircraftType;

    @Comment("한국어 스크립트")
    @Column(name = "script_korean", columnDefinition = "TEXT", nullable = false)
    private String scriptKorean;

    @Comment("영어 스크립트")
    @Column(name = "script_english", columnDefinition = "TEXT")
    private String scriptEnglish;

    @Comment("문제 해설")
    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Comment("사용 여부 (UNUSED/PRACTICE/EVALUATION)")
    @Column(name = "is_used", length = 10, nullable = false)
    @Builder.Default
    private String isUsed = "UNUSED";

    @Column(name = "status", length = 10, nullable = false) //추가: 문제 상태 (ACTIVE, DELETED)
    private String status;

    // Soft Delete 처리
    public void delete() {
        this.status = "INACTIVE";
    }

    // 활성화 처리
    public void activate() {
        this.status = "ACTIVE";
    }

    public QuestionStep2(String situationDescription, String aircraftType, String scriptKorean, String scriptEnglish, String explanation) {
        this.situationDescription = situationDescription;
        this.aircraftType = aircraftType;
        this.scriptKorean = scriptKorean;
        this.scriptEnglish = scriptEnglish;
        this.explanation = explanation;
        this.isUsed = "MOCK";
        this.status = "ACTIVE";
    }

    public void update(String situationDescription, String aircraftType, String scriptKorean, String scriptEnglish, String explanation) {
        this.situationDescription = situationDescription;
        this.aircraftType = aircraftType;
        this.scriptKorean = scriptKorean;
        this.scriptEnglish = scriptEnglish;
        this.explanation = explanation;
    }
}
