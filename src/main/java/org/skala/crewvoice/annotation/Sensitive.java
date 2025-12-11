package org.skala.crewvoice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 민감한 데이터를 표시하는 어노테이션
 * 로그, 디버그 출력 시 자동으로 마스킹 처리되어야 함을 나타냄
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {
}
