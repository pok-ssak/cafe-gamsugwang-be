package pokssak.gsg.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;
import pokssak.gsg.common.dto.ApiResponse;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.common.exception.ErrorCode;
import pokssak.gsg.common.exception.ErrorResponse;

@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            sendErrorResponse(response, e.getErrorCode());
        }

    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode)
        throws IOException {

        ErrorResponse errorResponse = ErrorResponse.builder()
            .message(errorCode.getMessage())
            .build();

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(errorResponse)));
        response.getWriter().flush();
        response.getWriter().close();
    }
}
