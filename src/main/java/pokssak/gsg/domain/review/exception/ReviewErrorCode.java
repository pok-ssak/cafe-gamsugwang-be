package pokssak.gsg.domain.review.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import pokssak.gsg.common.exception.ErrorCode;

@AllArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다."),
    ALREADY_POST_TODAY(HttpStatus.BAD_REQUEST, "오늘 이미 해당 가게에 리뷰를 작성했습니다.");

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
