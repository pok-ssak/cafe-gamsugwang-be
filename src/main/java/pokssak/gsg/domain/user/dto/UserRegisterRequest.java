package pokssak.gsg.domain.user.dto;

import pokssak.gsg.domain.user.entity.JoinType;

import java.util.List;

public record UserRegisterRequest(
        String nickName,
        String email,
        String password,
        String imageUrl,
        JoinType joinType,
        List<Long> keywordIds
) {
}
