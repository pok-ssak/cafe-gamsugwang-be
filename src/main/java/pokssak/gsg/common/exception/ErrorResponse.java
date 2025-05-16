package pokssak.gsg.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.validation.FieldError;

import java.util.List;

@Builder
public record ErrorResponse(

    String message,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<ValidationError> errors) {

    public record ValidationError(String field, String message) {

        public static ValidationError of(FieldError fieldError) {
            return new ValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
    }
}
