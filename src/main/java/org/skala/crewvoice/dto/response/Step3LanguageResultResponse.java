package org.skala.crewvoice.dto.response;

import lombok.Getter;
import org.skala.crewvoice.domain.EvaluationStep3;

@Getter
public class Step3LanguageResultResponse {

    private final String pronunciationScore;
    private final String pauseScore;
    private final String intonationScore;
    private final String toneScore;
    private final String speedScore;
    private final String accentScore;
    private final String overallScore;
    private final String aiComment;

    private Step3LanguageResultResponse(String pronunciationScore, String pauseScore,
                                        String intonationScore, String toneScore,
                                        String speedScore, String accentScore,
                                        String overallScore, String aiComment) {
        this.pronunciationScore = pronunciationScore;
        this.pauseScore = pauseScore;
        this.intonationScore = intonationScore;
        this.toneScore = toneScore;
        this.speedScore = speedScore;
        this.accentScore = accentScore;
        this.overallScore = overallScore;
        this.aiComment = aiComment;
    }

    /**
     * EvaluationStep3 엔티티로부터 Step3LanguageResultResponse 생성
     *
     * @param evaluation Step3 평가 결과 (한국어 또는 영어)
     * @return Step3LanguageResultResponse
     */
    public static Step3LanguageResultResponse from(EvaluationStep3 evaluation) {
        return new Step3LanguageResultResponse(
                evaluation.getScorePronunciation() != null ? evaluation.getScorePronunciation().toString() : "0",
                evaluation.getScorePause() != null ? evaluation.getScorePause().toString() : "0",
                evaluation.getScoreIntonation() != null ? evaluation.getScoreIntonation().toString() : "0",
                evaluation.getScoreTone() != null ? evaluation.getScoreTone().toString() : "0",
                evaluation.getScoreSpeed() != null ? evaluation.getScoreSpeed().toString() : "0",
                evaluation.getScoreAccent() != null ? evaluation.getScoreAccent().toString() : "0",
                evaluation.getOverallScore() != null ? evaluation.getOverallScore().toString() : "0",
                evaluation.getFeedbackSummary() != null ? evaluation.getFeedbackSummary() : "아직 평가가 완료되지 않았습니다."
        );
    }

    /**
     * 기본값(0점)으로 Step3LanguageResultResponse 생성
     * 평가 결과가 없을 때 사용
     *
     * @return 모든 점수가 0인 Step3LanguageResultResponse
     */
    public static Step3LanguageResultResponse empty() {
        return new Step3LanguageResultResponse(
                "0", "0", "0", "0", "0", "0", "0",
                "아직 평가가 완료되지 않았습니다."
        );
    }
}
