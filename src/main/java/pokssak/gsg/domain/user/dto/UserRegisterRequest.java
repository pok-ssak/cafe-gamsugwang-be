package pokssak.gsg.domain.user.dto;

import lombok.Builder;
import pokssak.gsg.common.vo.Keyword;
import pokssak.gsg.domain.user.entity.JoinType;

import java.util.List;

@Builder
public record UserRegisterRequest(
        String nickName,
        String email,
        String password,
        String imageUrl,
        JoinType joinType,
        List<Keyword> keywords
) {
}
