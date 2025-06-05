package pokssak.gsg.domain.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.admin.exception.AdminErrorCode;
import pokssak.gsg.domain.admin.repository.AdminRepository;
import pokssak.gsg.domain.cafe.entity.Cafe;
import pokssak.gsg.domain.cafe.entity.CafeKeyword;
import pokssak.gsg.domain.cafe.entity.Menu;
import pokssak.gsg.domain.cafe.entity.Suggestion;
import pokssak.gsg.domain.cafe.exception.CafeErrorCode;
import pokssak.gsg.domain.cafe.exception.SuggestionErrorCode;
import pokssak.gsg.domain.cafe.repository.CafeRepository;
import pokssak.gsg.domain.cafe.repository.KeywordRepository;
import pokssak.gsg.domain.cafe.repository.SuggestionRedisRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final CafeRepository cafeRepository;
    private final SuggestionRedisRepository suggestionRedisRepository;
    private final AdminRepository adminRepository;
    private final KeywordRepository keywordRepository;

    @Transactional
    public void acceptSuggestion(Long suggestionId, Long adminId) {
        Suggestion suggestion = suggestionRedisRepository.findById(suggestionId)
                .orElseThrow(() -> new CustomException(SuggestionErrorCode.SUGGESTION_NOT_FOUND));

        Cafe cafe = cafeRepository.findByIdWithMenusAndKeywords(suggestion.getOldCafeId())
                .orElseThrow(() -> new CustomException(CafeErrorCode.CAFE_NOT_FOUND));

        adminRepository.findById(adminId)
                        .orElseThrow(() -> new CustomException(AdminErrorCode.NOT_FOUND));

        Suggestion.NewCafeData newCafe = suggestion.getNewCafe();

        // 메뉴 엔티티 변환
        Set<Menu> menus = newCafe.getMenuList().stream()
                .map(menuData -> Menu.builder()
                        .name(menuData.getName())
                        .menuImageUrl(menuData.getMenuImageUrl())
                        .price(menuData.getPrice())
                        .modifier(menuData.getModifier())
                        .build())
                .collect(Collectors.toSet());

        // 키워드 엔티티 조회 및 연결
        Set<CafeKeyword> cafeKeywords = newCafe.getKeywordList().stream()
                .map(k -> keywordRepository.findById(k.getId())
                        .orElseThrow(() -> new CustomException(CafeErrorCode.KEYWORD_NOT_FOUND)))
                .collect(Collectors.toSet());

        // 업데이트 통합 처리
        cafe.updateFromSuggestion(newCafe, cafeKeywords, menus);

        cafeRepository.save(cafe);
        suggestionRedisRepository.deleteById(suggestionId);

        log.info("어드민({})이 수정 제안({})을 수락했습니다.", adminId, suggestionId);
    }

    public Page<Suggestion> getAllSuggestions(Pageable pageable) {
        List<Suggestion> suggestions = new ArrayList<>(suggestionRedisRepository.findAll());

        suggestions.sort(Comparator.comparing(Suggestion::getCreatedAt).reversed());

        int total = suggestions.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        if (start > end) {
            return new PageImpl<>(Collections.emptyList(), pageable, total);
        }

        List<Suggestion> pagedSuggestions = suggestions.subList(start, end);

        return new PageImpl<>(pagedSuggestions, pageable, total);
    }


}
