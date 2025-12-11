package org.skala.crewvoice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * 가중치 합계가 100인지 검증하는 커스텀 어노테이션
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WeightSumValidator.class)
@Documented
public @interface ValidWeightSum {
    String message() default "가중치 합계는 100이어야 합니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
