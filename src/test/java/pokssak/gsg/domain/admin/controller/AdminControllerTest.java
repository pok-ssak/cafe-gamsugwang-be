package pokssak.gsg.domain.admin.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import pokssak.gsg.domain.admin.entity.Admin;
import pokssak.gsg.domain.admin.service.AdminService;
import pokssak.gsg.domain.cafe.entity.Suggestion;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @Test
    @DisplayName("제안 수락 - 성공")
    void acceptSuggestion_success() {
        // given
        Long suggestionId = 1L;
        Admin admin = Admin.builder()
                .id(999L)
                .build();

        // when
        ResponseEntity<?> response = adminController.acceptSuggestion(suggestionId, admin);

        // then
        verify(adminService).acceptSuggestion(suggestionId, 999L);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    @DisplayName("제안 조회 - 성공")
    void getAllSuggestions_success() {
        Suggestion s1 = Suggestion.builder().id(1L).createdAt(LocalDateTime.now().minusDays(1)).build();
        Suggestion s2 = Suggestion.builder().id(2L).createdAt(LocalDateTime.now()).build();

        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Suggestion> suggestionPage = new PageImpl<>(List.of(s2, s1), pageable, 2);

        when(adminService.getAllSuggestions(any(Pageable.class))).thenReturn(suggestionPage);

        // when
        ResponseEntity<?> response = adminController.getAllSuggestions(0, 20);

        // then
        verify(adminService).getAllSuggestions(any(Pageable.class));
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }
}
