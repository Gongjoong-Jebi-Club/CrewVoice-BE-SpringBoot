package org.skala.crewvoice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.skala.crewvoice.dto.request.EvaluationCreateRequest;

import java.time.OffsetDateTime;

/**
 * 날짜 범위 검증 로직
 */
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, EvaluationCreateRequest> {

    @Override
    public boolean isValid(EvaluationCreateRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getStartDate() == null || request.getEndDate() == null) {
            return true; // null은 @NotNull이 처리
        }

        context.disableDefaultConstraintViolation();

        // 1. 시작 날짜가 종료 날짜보다 이후인지 검증
        if (request.getStartDate().isAfter(request.getEndDate())) {
            context.buildConstraintViolationWithTemplate(
                    "시작 날짜는 종료 날짜보다 이전이어야 합니다"
            ).addPropertyNode("startDate").addConstraintViolation();
            return false;
        }

        // 2. 시작 날짜가 현재 시간 이전인지 검증
        if (request.getStartDate().isBefore(OffsetDateTime.now())) {
            context.buildConstraintViolationWithTemplate(
                    "시작 날짜는 현재 시간 이후여야 합니다"
            ).addPropertyNode("startDate").addConstraintViolation();
            return false;
        }

        return true;
    }
}
