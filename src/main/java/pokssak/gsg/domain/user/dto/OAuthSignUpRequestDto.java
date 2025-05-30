package pokssak.gsg.domain.user.dto;

import java.util.Set;
import pokssak.gsg.common.vo.Keyword;

public record OAuthSignUpRequestDto(String nickname, Set<Keyword> keywords) {

}
