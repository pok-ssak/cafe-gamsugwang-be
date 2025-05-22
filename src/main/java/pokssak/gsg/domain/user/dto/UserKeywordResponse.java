package pokssak.gsg.domain.user.dto;

import pokssak.gsg.domain.user.entity.UserKeyword;

public record UserKeywordResponse(
        Long id,
        String word,
        long count
) {
    public static UserKeywordResponse from(UserKeyword userKeyword) {
        return new UserKeywordResponse(
                userKeyword.getId(),
                userKeyword.getKeyword().word(),
                userKeyword.getKeyword().count()
        );
    }
}

