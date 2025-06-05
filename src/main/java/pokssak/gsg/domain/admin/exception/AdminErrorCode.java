package pokssak.gsg.domain.admin.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import pokssak.gsg.common.exception.ErrorCode;

@AllArgsConstructor
public enum AdminErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 관리자입니다."),
    INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "올바르지 않은 비밀번호 입니다."),
    USER_EMAIL_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 존재하는 이메일 입니다.");

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
