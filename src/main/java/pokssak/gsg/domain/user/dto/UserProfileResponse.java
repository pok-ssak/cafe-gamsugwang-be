package pokssak.gsg.domain.user.dto;

import lombok.Builder;
import pokssak.gsg.common.vo.Keyword;
import pokssak.gsg.domain.user.entity.JoinType;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.entity.UserKeyword;

import java.util.List;

@Builder
public record UserProfileResponse(
        Long id,
        String nickName,
        String email,
        String imageUrl,
        JoinType joinType,

        List<Keyword> keywords,
        int bookmarkCount,
        int reviewCount
) {
    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .joinType(user.getJoinType())
                .keywords(user.getUserKeywords().stream().map(UserKeyword::getKeyword).toList())
                .bookmarkCount(user.getBookmarks() != null ? user.getBookmarks().size() : 0)
                .reviewCount(user.getReviews() != null ? user.getReviews().size() : 0)
                .build();
    }
}
