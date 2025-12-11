package org.skala.crewvoice.exception.custom;

import lombok.Getter;
import org.skala.crewvoice.exception.ErrorCode;

/**
 * 인증 정보(비밀번호, 토큰 등)가 유효하지 않을 때 발생하는 예외
 */
@Getter
public class InvalidCredentialsException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidCredentialsException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public InvalidCredentialsException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
