package pokssak.gsg.domain.review.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import pokssak.gsg.common.exception.ErrorCode;

@AllArgsConstructor
public enum ReviewLikeErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰좋아요입니다."),
    ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 좋아요한 게시물입니다.");

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
