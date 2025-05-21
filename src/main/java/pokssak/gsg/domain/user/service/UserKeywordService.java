package pokssak.gsg.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.common.vo.Keyword;
import pokssak.gsg.domain.user.dto.UserKeywordResponse;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.entity.UserKeyword;
import pokssak.gsg.domain.user.exception.UserErrorCode;
import pokssak.gsg.domain.user.repository.UserKeywordRepository;
import pokssak.gsg.domain.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserKeywordService {

    private final UserRepository userRepository;
    private final UserKeywordRepository userKeywordRepository;

    // 키워드 조회
    @Transactional(readOnly = true)
    public List<UserKeywordResponse> getUserKeywords(Long userId) {
        List<UserKeyword> userKeywords = userKeywordRepository.findByUserId(userId);
        return userKeywords.stream()
                .map(UserKeywordResponse::from)
                .toList();
    }

    // 회원가입 시 키워드 생성
    @Transactional
    public void addUserKeywords(Long userId, List<Keyword> keywords) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));

        // 새 키워드 추가
        List<UserKeyword> newUserKeywords = keywords.stream()
                .map(keyword -> UserKeyword.builder()
                        .user(user)
                        .keyword(keyword)
                        .build())
                .collect(Collectors.toList());

        userKeywordRepository.saveAll(newUserKeywords);
    }

    // 키워드 수정
    @Transactional
    public void updateUserKeywords(Long userId, List<Keyword> keywords) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));

        // 기존 키워드 모두 삭제
        userKeywordRepository.deleteByUser(user);

        // 새 키워드 추가
        List<UserKeyword> newUserKeywords = keywords.stream()
                .map(keyword -> UserKeyword.builder()
                        .user(user)
                        .keyword(keyword)
                        .build())
                .collect(Collectors.toList());

        userKeywordRepository.saveAll(newUserKeywords);
    }
}
