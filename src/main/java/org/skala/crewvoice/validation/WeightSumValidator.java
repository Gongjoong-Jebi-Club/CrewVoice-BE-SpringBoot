package org.skala.crewvoice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.skala.crewvoice.dto.request.EvaluationCreateRequest;

/**
 * 가중치 합계 검증 로직
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
