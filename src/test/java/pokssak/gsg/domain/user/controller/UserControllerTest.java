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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
        UserRegisterRequest request = UserRegisterRequest.builder()
                .nickName("testuser")
                .email("test@example.com")
                .password("plaintext")
                .imageUrl("http://image.com/profile.jpg")
                .joinType(JoinType.LOCAL)
                .keywords(List.of())
                .build();

        UserResponse response = UserResponse.builder()
                .nickName("testuser")
                .email("test@example.com")
                .build();

        when(userService.register(any(UserRegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
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
