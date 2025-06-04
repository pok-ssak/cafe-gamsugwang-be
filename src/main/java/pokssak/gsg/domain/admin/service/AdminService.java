package pokssak.gsg.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.cafe.entity.Cafe;
import pokssak.gsg.domain.cafe.entity.Suggestion;
import pokssak.gsg.domain.cafe.exception.CafeErrorCode;
import pokssak.gsg.domain.cafe.repository.CafeRepository;
import pokssak.gsg.domain.cafe.repository.SuggestionRedisRepository;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CafeRepository cafeRepository;
    private final SuggestionRedisRepository suggestionRedisRepository;

    public void acceptSuggestion(Long suggestionId, Long adminId) {
        Suggestion suggestion = suggestionRedisRepository.findById(suggestionId)
                .orElseThrow(() -> new CustomException(CafeErrorCode.CAFE_NOT_FOUND));

        // 원본 카페 가져오기 (DB)
        Cafe oldCafe = cafeRepository.findByIdWithMenusAndKeywords(suggestion.getOldCafe().getId())
                .orElseThrow(() -> new CustomException(CafeErrorCode.CAFE_NOT_FOUND));

        // Suggestion의 newCafe 정보로 oldCafe 업데이트 (setter or builder로 변경)
//        oldCafe.updateFromSuggestion(suggestion.getCafe());

        // 변경된 카페 저장
        cafeRepository.save(oldCafe);

        // Redis에서 제안 삭제 혹은 상태 변경
        suggestionRedisRepository.delete(suggestion);
    }

}
