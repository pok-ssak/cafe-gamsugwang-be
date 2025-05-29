package pokssak.gsg.domain.bookmark.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import pokssak.gsg.common.exception.ErrorCode;

@AllArgsConstructor
public enum BookmarkErrorCode implements ErrorCode {

    BOOKMARK_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 존재하는 관심목록입니다.");

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
