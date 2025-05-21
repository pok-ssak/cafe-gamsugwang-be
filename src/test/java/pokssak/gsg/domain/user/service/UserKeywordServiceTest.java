package pokssak.gsg.domain.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.common.vo.Keyword;
import pokssak.gsg.domain.user.dto.UserKeywordResponse;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.entity.UserKeyword;
import pokssak.gsg.domain.user.exception.UserErrorCode;
import pokssak.gsg.domain.user.repository.UserKeywordRepository;
import pokssak.gsg.domain.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserKeywordServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserKeywordRepository userKeywordRepository;

    @InjectMocks
    private UserKeywordService userKeywordService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder().id(1L).nickName("테스트").build();
    }

    @Test
    @DisplayName("사용자 키워드 조회 - 성공")
    void getUserKeywords_success() {
        // given
        List<UserKeyword> mockKeywords = List.of(
                UserKeyword.builder()
                        .id(1L)
                        .user(mockUser)
                        .keyword(new Keyword("조용한", 0))
                        .build(),
                UserKeyword.builder()
                        .id(2L)
                        .user(mockUser)
                        .keyword(new Keyword("24시간", 0))
                        .build()
        );

        when(userKeywordRepository.findByUserId(1L)).thenReturn(mockKeywords);

        // when
        List<UserKeywordResponse> result = userKeywordService.getUserKeywords(1L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).word()).isEqualTo("조용한");
        verify(userKeywordRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("사용자 키워드 수정 - 성공")
    void updateUserKeywords_success() {
        // given
        List<Keyword> newKeywords = List.of(
                new Keyword("조용한", 0),
                new Keyword("24시간", 0)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // when
        userKeywordService.updateUserKeywords(1L, newKeywords);

        // then
        verify(userRepository).findById(1L);
        verify(userKeywordRepository).deleteByUser(mockUser);
        verify(userKeywordRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("사용자 키워드 수정 - 유저 없음")
    void updateUserKeywords_userNotFound() {
        // given
        List<Keyword> newKeywords = List.of(new Keyword("조용한", 0));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        Throwable thrown = catchThrowable(() -> userKeywordService.updateUserKeywords(999L, newKeywords));

        assertThat(thrown).isInstanceOf(CustomException.class);
        CustomException customEx = (CustomException) thrown;
        assertThat(customEx.getErrorCode()).isEqualTo(UserErrorCode.NOT_FOUND);

        verify(userRepository).findById(999L);
        verify(userKeywordRepository, never()).deleteByUser(any());
    }
}
