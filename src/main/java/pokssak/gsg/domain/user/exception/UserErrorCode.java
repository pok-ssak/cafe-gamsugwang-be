package pokssak.gsg.domain.user.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import pokssak.gsg.common.exception.ErrorCode;

@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 계정입니다."),
    INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "올바르지 않은 비밀번호 입니다."),
    ALREADY_ACTIVE(HttpStatus.BAD_REQUEST, "이미 복구된 계정입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
