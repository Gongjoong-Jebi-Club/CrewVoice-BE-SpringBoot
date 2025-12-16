package org.skala.crewvoice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * FastAPI 파라미터 가중치 검증 커스텀 어노테이션
 * - Tone 4개 합계 = 1.0
 * - Pause 3개 합계 = 1.0
 * - Accent 2개 합계 = 1.0
 * - Intonation 2개 합계 = 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FastAPIWeightsValidator.class)
@Documented
public @interface ValidFastAPIWeights {
    String message() default "FastAPI 파라미터 가중치가 유효하지 않습니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
