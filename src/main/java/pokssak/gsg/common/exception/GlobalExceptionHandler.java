package pokssak.gsg.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pokssak.gsg.common.dto.ApiResponse;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> customExceptionHandler(CustomException ex) {
        log.info("Custom Exception: {}", ex.getErrorCode().getMessage());
        ErrorCode errorCode = ex.getErrorCode();

        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exceptionHandler(Exception ex) {
        log.error("Internal Server Error: {}", ex.getMessage(), ex);
        return handleExceptionInternal(GlobalErrorCode.INTERNAL_SERVER);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        return ResponseEntity.status(GlobalErrorCode.BAD_REQUEST.getHttpStatus()).body(ApiResponse.fail(makeErrorResponse(ex)));
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.fail(errorResponse));
    }

    private ErrorResponse makeErrorResponse(BindException ex) {
        List<ErrorResponse.ValidationError> errorList = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ErrorResponse.ValidationError::of)
                .toList();

        return ErrorResponse.builder()
                .message(GlobalErrorCode.BAD_REQUEST.getMessage())
                .errors(errorList)
                .build();
    }
}
