package org.skala.crewvoice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.skala.crewvoice.dto.request.EvaluationCreateRequest;

/**
 * 평가 항목 가중치 합계 검증 로직.
 * <p>
 * 발음, 억양, 강세, 끊어읽기, 어조, 속도 등 6가지 내부 평가 항목의 가중치 합이
 * 정확히 100이 되는지 확인합니다. (Percentage 기준)
 */
public class WeightSumValidator implements ConstraintValidator<ValidWeightSum, EvaluationCreateRequest> {

    @Override
    public boolean isValid(EvaluationCreateRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true; // null은 @NotNull이 처리
        }

        int sum = request.getWeightPronunciation() + request.getWeightIntonation()
                + request.getWeightAccent() + request.getWeightPause()
                + request.getWeightTone() + request.getWeightSpeed();

        if (sum != 100) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "가중치 합계는 100이어야 합니다. 현재 합계: " + sum
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
