package pokssak.gsg.domain.cafe.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import pokssak.gsg.common.exception.ErrorCode;

@AllArgsConstructor
public enum KeywordErrorCode implements ErrorCode {
    KEYWORD_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 키워드입니다.");

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
