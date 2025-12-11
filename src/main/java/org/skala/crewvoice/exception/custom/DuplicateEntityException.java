package org.skala.crewvoice.exception.custom;

import lombok.Getter;
import org.skala.crewvoice.exception.ErrorCode;

/**
 * 중복된 엔티티(이메일, 사용자 등) 생성 시도 시 발생하는 예외
 */
@Getter
public class DuplicateEntityException extends RuntimeException {

    private final ErrorCode errorCode;

    public DuplicateEntityException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public DuplicateEntityException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
