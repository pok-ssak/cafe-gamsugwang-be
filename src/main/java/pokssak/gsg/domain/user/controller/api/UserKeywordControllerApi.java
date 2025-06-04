package pokssak.gsg.domain.user.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.domain.user.dto.KeywordRequest;
import pokssak.gsg.domain.user.dto.UserKeywordResponse;
import pokssak.gsg.domain.user.entity.User;

import java.util.List;

@Tag(name = "User Keyword", description = "사용자 키워드 관련 API")
@RequestMapping("/api/users/{userId}/keywords")
public interface UserKeywordControllerApi {

    @Operation(
            summary = "사용자 키워드 조회",
            description = "인증된 사용자의 키워드 목록을 조회합니다.",
            parameters = {
                    @Parameter(name = "userId", description = "사용자 ID", example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserKeywordResponse.class)
                            )
                    )
            }
    )
    @GetMapping
    ResponseEntity<List<UserKeywordResponse>> getUserKeywords(
            @Parameter(hidden = true) @AuthenticationPrincipal User user
    );

//    @Operation(
//            summary = "사용자 키워드 수정",
//            description = "인증된 사용자의 키워드 목록을 수정합니다. 기존 키워드를 전부 대체합니다.",
//            requestBody = @RequestBody(
//                    required = true,
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = KeywordRequest.class),
//                            examples = @ExampleObject(value = """
//                    {
//                      "keywords": [
//                        {
//                          "word": "spring",
//                          "count": 3
//                        },
//                        {
//                          "word": "java",
//                          "count": 5
//                        }
//                      ]
//                    }
//                """)
//                    )
//            ),
//            parameters = {
//                    @Parameter(name = "userId", description = "사용자 ID", example = "1")
//            },
//            responses = {
//                    @ApiResponse(responseCode = "204", description = "수정 성공"),
//                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
//            }
//    )
//    @PutMapping
//    ResponseEntity<Void> updateUserKeywords(
//            @Parameter(hidden = true) @AuthenticationPrincipal User user,
//            @org.springframework.web.bind.annotation.RequestBody KeywordRequest request
//    );
}