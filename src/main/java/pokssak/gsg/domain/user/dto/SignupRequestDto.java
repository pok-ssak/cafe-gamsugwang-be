package pokssak.gsg.domain.user.dto;

import java.util.Set;
import pokssak.gsg.common.vo.Keyword;

public record SignupRequestDto(String email, String password, String nickname, Set<Keyword> keywords
) {

}
