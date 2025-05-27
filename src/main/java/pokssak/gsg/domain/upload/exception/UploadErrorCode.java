package pokssak.gsg.domain.upload.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import pokssak.gsg.common.exception.ErrorCode;

@AllArgsConstructor
public enum UploadErrorCode implements ErrorCode {

    NOT_AN_IMAGE(HttpStatus.BAD_REQUEST, "이미지파일이 아닙니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() { return httpStatus; }

    @Override
    public String getMessage() { return message; }
}
