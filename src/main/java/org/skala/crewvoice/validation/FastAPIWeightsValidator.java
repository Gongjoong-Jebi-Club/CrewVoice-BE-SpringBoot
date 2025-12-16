package org.skala.crewvoice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.skala.crewvoice.dto.request.EvaluationCreateRequest;

import java.math.BigDecimal;

/**
 * FastAPI 파라미터 가중치 검증 로직
 * - 각 그룹별 합계가 1.0인지 검증
 * - 허용 오차: ±0.001 (부동소수점 연산 오차 고려)
 */
public class FastAPIWeightsValidator implements ConstraintValidator<ValidFastAPIWeights, EvaluationCreateRequest> {

    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final BigDecimal TOLERANCE = new BigDecimal("0.001");

    @Override
    public boolean isValid(EvaluationCreateRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true; // null은 @NotNull이 처리
        }

        context.disableDefaultConstraintViolation();

        // Tone 검증
        BigDecimal toneSum = request.getWeightClarity()
                .add(request.getWeightStability())
                .add(request.getWeightBrightness())
                .add(request.getWeightPitchPosition());
        if (!isApproximatelyEqual(toneSum, ONE)) {
            context.buildConstraintViolationWithTemplate(
                    "Tone 가중치 합계는 1.0이어야 합니다. (현재: " + toneSum + ")"
            ).addConstraintViolation();
            return false;
        }

        // Pause 검증
        BigDecimal pauseSum = request.getWeightFluency()
                .add(request.getWeightPosition())
                .add(request.getWeightLength());
        if (!isApproximatelyEqual(pauseSum, ONE)) {
            context.buildConstraintViolationWithTemplate(
                    "Pause 가중치 합계는 1.0이어야 합니다. (현재: " + pauseSum + ")"
            ).addConstraintViolation();
            return false;
        }

        // Accent 검증
        BigDecimal accentSum = request.getWeightPitch().add(request.getWeightEnergy());
        if (!isApproximatelyEqual(accentSum, ONE)) {
            context.buildConstraintViolationWithTemplate(
                    "Accent 가중치 합계는 1.0이어야 합니다. (현재: " + accentSum + ")"
            ).addConstraintViolation();
            return false;
        }

        // Intonation 검증
        BigDecimal intonationSum = request.getWeightBaseline().add(request.getWeightIso());
        if (!isApproximatelyEqual(intonationSum, ONE)) {
            context.buildConstraintViolationWithTemplate(
                    "Intonation 가중치 합계는 1.0이어야 합니다. (현재: " + intonationSum + ")"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }

    /**
     * 부동소수점 비교 (허용 오차 ±0.001)
     */
    private boolean isApproximatelyEqual(BigDecimal a, BigDecimal b) {
        return a.subtract(b).abs().compareTo(TOLERANCE) <= 0;
    }
}
