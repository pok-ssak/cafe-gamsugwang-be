package pokssak.gsg.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import pokssak.gsg.common.s3.S3Uploader;
import pokssak.gsg.domain.user.dto.UserRegisterRequest;
import pokssak.gsg.domain.user.dto.UserResponse;
import pokssak.gsg.domain.user.entity.JoinType;
import pokssak.gsg.domain.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private S3Uploader s3Uploader;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void register_success() throws Exception {
        // JSON 문자열로 보낼 UserRegisterRequest 일부 필드들
        String jsonRequest = objectMapper.writeValueAsString(
                UserRegisterRequest.builder()
                        .nickName("testuser")
                        .email("test@example.com")
                        .password("plaintext")
                        .joinType(JoinType.LOCAL)
                        .keywords(List.of()) // 필요한 키워드 리스트
                        .build()
        );

        // MultipartFile 모킹 (프로필 이미지)
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",                            // form-data 이름
                "profile.jpg",                     // 파일명
                "image/jpeg",                      // Content-Type
                "fake-image-content".getBytes()    // 파일 내용 (테스트용 더미 바이트)
        );

        // request 필드를 JSON 문자열 형태로 보내기 위한 MockMultipartFile
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",                        // form-data 이름 (컨트롤러에서 @RequestPart("request")로 받을 때 이름 맞춰야 함)
                "",                              // 파일명 없어도 됨
                "application/json",              // Content-Type
                jsonRequest.getBytes()           // JSON 바이트
        );

        UserResponse response = UserResponse.builder()
                .nickName("testuser")
                .email("test@example.com")
                .build();

        when(userService.register(any(UserRegisterRequest.class))).thenReturn(response);

        mockMvc.perform(multipart("/api/v1/register")
                        .file(requestPart)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickName").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).register(any(UserRegisterRequest.class));
    }

    @Test
    @DisplayName("회원탈퇴 성공")
    void deleteUser_success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("회원복구 성공")
    void restoreUser_success() throws Exception {
        doNothing().when(userService).restoreUser(1L);

        mockMvc.perform(put("/api/v1/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).restoreUser(1L);
    }
}
