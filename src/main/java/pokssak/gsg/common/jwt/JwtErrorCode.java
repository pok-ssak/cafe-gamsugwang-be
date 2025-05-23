package pokssak.gsg.common.jwt;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import pokssak.gsg.common.exception.ErrorCode;

@AllArgsConstructor
public enum JwtErrorCode implements ErrorCode {

    EXPIRE_ERROR(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰 입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 토큰 입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원하지 않는 형식의 토큰 입니다."),
    NOT_FOUND_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 비어있습니다.");

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

