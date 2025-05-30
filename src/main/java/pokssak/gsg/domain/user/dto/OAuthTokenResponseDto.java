package pokssak.gsg.domain.user.dto;

import pokssak.gsg.common.jwt.JwtTokenDto;

public record OAuthTokenResponseDto(boolean isRegister, JwtTokenDto jwtTokenDto) {

}
