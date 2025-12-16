package org.skala.crewvoice.dto.response;

import lombok.Getter;
import org.skala.crewvoice.domain.EvaluationStep2;

@Getter
public class Step2LanguageResultResponse {

    private final String pronunciationScore;
    private final String pauseScore;
    private final String intonationScore;
    private final String toneScore;
    private final String speedScore;
    private final String accentScore;
    private final String overallScore;
    private final String aiComment;

    private Step2LanguageResultResponse(String pronunciationScore, String pauseScore,
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
     * EvaluationStep2 엔티티로부터 Step2LanguageResultResponse 생성
     *
     * @param evaluation Step2 평가 결과 (한국어 또는 영어)
     * @return Step2LanguageResultResponse
     */
    public static Step2LanguageResultResponse from(EvaluationStep2 evaluation) {
        return new Step2LanguageResultResponse(
                evaluation.getScorePronunciation().toString(),
                evaluation.getScorePause().toString(),
                evaluation.getScoreIntonation().toString(),
                evaluation.getScoreTone().toString(),
                evaluation.getScoreSpeed().toString(),
                evaluation.getScoreAccent().toString(),
                evaluation.getOverallScore().toString(),
                evaluation.getFeedbackSummary()
        );
    }

    /**
     * 기본값(0점)으로 Step2LanguageResultResponse 생성
     * 평가 결과가 없을 때 사용
     *
     * @return 모든 점수가 0인 Step2LanguageResultResponse
     */
    public static Step2LanguageResultResponse empty() {
        return new Step2LanguageResultResponse(
                "0", "0", "0", "0", "0", "0", "0",
                "아직 평가가 완료되지 않았습니다."
        );
    }
}
