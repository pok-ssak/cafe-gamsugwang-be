package pokssak.gsg.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import pokssak.gsg.common.exception.ErrorResponse;

public record ApiResponse<T>(
    boolean success,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    T data,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ErrorResponse error
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> fail(ErrorResponse error) {
        return new ApiResponse<>(false, null, error);
    }
}
