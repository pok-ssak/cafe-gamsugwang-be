package pokssak.gsg.domain.admin.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.admin.entity.Admin;
import pokssak.gsg.domain.admin.exception.AdminErrorCode;
import pokssak.gsg.domain.admin.repository.AdminRepository;
import pokssak.gsg.domain.cafe.entity.Cafe;
import pokssak.gsg.domain.cafe.entity.Suggestion;
import pokssak.gsg.domain.cafe.entity.Suggestion.NewCafeData;
import pokssak.gsg.domain.cafe.exception.CafeErrorCode;
import pokssak.gsg.domain.cafe.exception.SuggestionErrorCode;
import pokssak.gsg.domain.cafe.repository.CafeRepository;
import pokssak.gsg.domain.cafe.repository.SuggestionRedisRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private CafeRepository cafeRepository;

    @Mock
    private SuggestionRedisRepository suggestionRedisRepository;

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    @DisplayName("제안수락 - 성공")
    void acceptSuggestion_success() {
        // given
        Long suggestionId = 1L;
        Long adminId = 999L;
        Long cafeId = 100L;

        NewCafeData newData = NewCafeData.builder()
                .title("Updated Title")
                .build();

        Suggestion suggestion = Suggestion.builder()
                .id(suggestionId)
                .oldCafeId(cafeId)
                .newCafe(newData)
                .build();

        Cafe cafe = mock(Cafe.class);
        Admin admin = new Admin();

        when(suggestionRedisRepository.findById(suggestionId)).thenReturn(Optional.of(suggestion));
        when(cafeRepository.findByIdWithMenusAndKeywords(cafeId)).thenReturn(Optional.of(cafe));
        when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));

        // when
        adminService.acceptSuggestion(suggestionId, adminId);

        // then
        verify(suggestionRedisRepository).findById(suggestionId);
        verify(cafeRepository).findByIdWithMenusAndKeywords(cafeId);
        verify(adminRepository).findById(adminId);
        verify(cafe).updateFromSuggestion(newData);
        verify(cafeRepository).save(cafe);
        verify(suggestionRedisRepository).deleteById(suggestionId);
    }

    @Test
    @DisplayName("제안이 존재하지 않으면 예외 발생")
    void acceptSuggestion_SuggestionNotFound() {
        Long suggestionId = 1L;
        Long adminId = 999L;

        when(suggestionRedisRepository.findById(suggestionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.acceptSuggestion(suggestionId, adminId))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException ce = (CustomException) ex;
                    assert ce.getErrorCode() == SuggestionErrorCode.SUGGESTION_NOT_FOUND;
                });

        verify(suggestionRedisRepository).findById(suggestionId);
        verifyNoMoreInteractions(cafeRepository);
    }

    @Test
    @DisplayName("카페가 존재하지 않으면 예외 발생")
    void acceptSuggestion_CafeNotFound() {
        Long suggestionId = 1L;
        Long adminId = 999L;
        Long cafeId = 100L;

        NewCafeData newData = NewCafeData.builder().title("test").build();

        Suggestion suggestion = Suggestion.builder()
                .id(suggestionId)
                .oldCafeId(cafeId)
                .newCafe(newData)
                .build();

        when(suggestionRedisRepository.findById(suggestionId)).thenReturn(Optional.of(suggestion));
        when(cafeRepository.findByIdWithMenusAndKeywords(cafeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.acceptSuggestion(suggestionId, adminId))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException ce = (CustomException) ex;
                    assert ce.getErrorCode() == CafeErrorCode.CAFE_NOT_FOUND;
                });

        verify(suggestionRedisRepository).findById(suggestionId);
        verify(cafeRepository).findByIdWithMenusAndKeywords(cafeId);
    }

    @Test
    @DisplayName("관리자가 존재하지 않으면 예외 발생")
    void acceptSuggestion_AdminNotFound() {
        Long suggestionId = 1L;
        Long adminId = 999L;
        Long cafeId = 100L;

        NewCafeData newData = NewCafeData.builder()
                .title("test")
                .build();

        Suggestion suggestion = Suggestion.builder()
                .id(suggestionId)
                .oldCafeId(cafeId)
                .newCafe(newData)
                .build();

        when(suggestionRedisRepository.findById(suggestionId)).thenReturn(Optional.of(suggestion));
        when(cafeRepository.findByIdWithMenusAndKeywords(cafeId)).thenReturn(Optional.of(mock(Cafe.class)));
        when(adminRepository.findById(adminId)).thenReturn(Optional.empty());  // admin 없음

        assertThatThrownBy(() -> adminService.acceptSuggestion(suggestionId, adminId))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException ce = (CustomException) ex;
                    assert ce.getErrorCode() == AdminErrorCode.NOT_FOUND;
                });

        verify(suggestionRedisRepository).findById(suggestionId);
        verify(cafeRepository).findByIdWithMenusAndKeywords(cafeId);
        verify(adminRepository).findById(adminId);
    }

    @Test
    @DisplayName("제안 조회 - 성공")
    void getAllSuggestions_success() {
        // given
        Suggestion oldSuggestion = Suggestion.builder()
                .id(1L)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();

        Suggestion newSuggestion = Suggestion.builder()
                .id(2L)
                .createdAt(LocalDateTime.now())
                .build();

        List<Suggestion> mockList = Arrays.asList(oldSuggestion, newSuggestion);
        when(suggestionRedisRepository.findAll()).thenReturn(mockList);

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<Suggestion> result = adminService.getAllSuggestions(pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getId()).isEqualTo(2L); // 최신 것이 먼저
        assertThat(result.getContent().get(1).getId()).isEqualTo(1L);

        verify(suggestionRedisRepository, times(1)).findAll();
    }
}
