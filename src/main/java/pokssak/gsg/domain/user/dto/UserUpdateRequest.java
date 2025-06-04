package pokssak.gsg.domain.user.dto;

import lombok.Builder;
import pokssak.gsg.common.vo.Keyword;
import pokssak.gsg.domain.user.entity.JoinType;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.entity.UserKeyword;

import java.util.List;

@Builder
public record UserUpdateRequest(
        String nickname,
        List<String> keywords
) {
}
