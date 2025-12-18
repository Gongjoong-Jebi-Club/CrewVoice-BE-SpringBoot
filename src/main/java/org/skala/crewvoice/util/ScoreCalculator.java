package org.skala.crewvoice.util;

import org.skala.crewvoice.domain.Evaluation;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 점수 계산 유틸리티 클래스
 * 가중 평균 점수를 계산합니다.
 */
public class ScoreCalculator {

    /**
     * 가중 평균 점수 계산
     * 각 항목 점수에 가중치를 곱한 후 합산 (가중치 합 = 100)
     *
     * @param evaluation 평가 (가중치 정보 포함)
     * @param scorePronunciation 발음 점수
     * @param scorePause 쉼 점수
     * @param scoreIntonation 억양 점수
     * @param scoreTone 톤 점수
     * @param scoreSpeed 속도 점수
     * @param scoreAccent 강세 점수
     * @return 가중 평균 점수
     */
    public static BigDecimal calculateWeightedScore(
            Evaluation evaluation,
            BigDecimal scorePronunciation,
            BigDecimal scorePause,
            BigDecimal scoreIntonation,
            BigDecimal scoreTone,
            BigDecimal scoreSpeed,
            BigDecimal scoreAccent
    ) {
        BigDecimal weightedSum = BigDecimal.ZERO;

        // 각 항목 점수 × 가중치 계산
        BigDecimal weightedPronunciation = scorePronunciation.multiply(evaluation.getWeightPronunciation());
        BigDecimal weightedPause = scorePause.multiply(evaluation.getWeightPause());
        BigDecimal weightedIntonation = scoreIntonation.multiply(evaluation.getWeightIntonation());
        BigDecimal weightedTone = scoreTone.multiply(evaluation.getWeightTone());
        BigDecimal weightedSpeed = scoreSpeed.multiply(evaluation.getWeightSpeed());
        BigDecimal weightedAccent = scoreAccent.multiply(evaluation.getWeightAccent());

        weightedSum = weightedSum.add(weightedPronunciation);
        weightedSum = weightedSum.add(weightedPause);
        weightedSum = weightedSum.add(weightedIntonation);
        weightedSum = weightedSum.add(weightedTone);
        weightedSum = weightedSum.add(weightedSpeed);
        weightedSum = weightedSum.add(weightedAccent);

        // 가중치 합계가 100이므로 100으로 나눔
        return weightedSum.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
