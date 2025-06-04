package pokssak.gsg.domain.user.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import pokssak.gsg.common.exception.ErrorCode;

@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    UNSUPPORTED_SOCIAL_LOGIN(HttpStatus.BAD_REQUEST, "지원 하지 않는 소셜 로그인 입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "올바르지 않은 refresh token 입니다");


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
