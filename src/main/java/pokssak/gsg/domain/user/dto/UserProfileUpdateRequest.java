package pokssak.gsg.domain.user.dto;

public record UserProfileUpdateRequest(
        String nickName,
        String imageUrl
) {
}
