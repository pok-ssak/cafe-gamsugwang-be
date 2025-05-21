package pokssak.gsg.domain.user.dto;

import lombok.Builder;
import pokssak.gsg.domain.user.entity.JoinType;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.entity.UserKeyword;

import java.util.List;

@Builder
public record UserResponse(
        String nickName,
        String email,
        String password,
        String imageUrl,
        JoinType joinType,
        List<Long> keywordIds
) {
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .nickName(user.getNickName())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .joinType(user.getJoinType())
                .keywordIds(user.getUserKeywords().stream()
                        .map(UserKeyword::getId)
                        .toList())
                .build();
    }
}
