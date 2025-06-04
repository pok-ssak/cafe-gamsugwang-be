package pokssak.gsg.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.admin.service.AdminDetailsService;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class AdminJwtTokenProvider {

    private final long tokenExpireSeconds;
    private final Key key;
    private final AdminDetailsService adminDetailsService;
    private final RedisTemplate<String, Object> redisTemplate;

    public AdminJwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                                 @Value("${jwt.token-expire-seconds}") long tokenExpireSeconds,
                                 RedisTemplate<String, Object> redisTemplate,
                                 AdminDetailsService adminDetailsService) {
        this.tokenExpireSeconds = tokenExpireSeconds;
        this.redisTemplate = redisTemplate;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.adminDetailsService = adminDetailsService;
    }

    public JwtTokenDto createToken(Long adminId) {
        long now = (new Date()).getTime();
        Date expiry = new Date(now + tokenExpireSeconds);
        Date refreshTokenExpiry = new Date(now + tokenExpireSeconds * 2 * 30);

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(adminId))
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(expiry)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(adminId))
                .setExpiration(refreshTokenExpiry)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        redisTemplate.opsForValue().set(
                getRefreshTokenKey(adminId),
                refreshToken,
                refreshTokenExpiry.getTime() - now,
                TimeUnit.MILLISECONDS
        );

        return JwtTokenDto.of(accessToken, refreshToken);
    }

    public Authentication getAuthentication(String token) {
        String adminId = getClaims(token).getSubject();
        UserDetails adminDetails = adminDetailsService.loadUserByUsername(adminId);
        return new UsernamePasswordAuthenticationToken(adminDetails, "", adminDetails.getAuthorities());
    }

    public Claims getClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException e) {
            throw new CustomException(JwtErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new CustomException(JwtErrorCode.EXPIRE_ERROR);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(JwtErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CustomException(JwtErrorCode.NOT_FOUND_TOKEN);
        }
    }

    public boolean isRefreshTokenValid(Long adminId, String refreshToken) {
        Object storedToken = redisTemplate.opsForValue().get(getRefreshTokenKey(adminId));
        return storedToken != null && refreshToken.equals(storedToken.toString());
    }

    public void deleteRefreshToken(Long adminId) {
        redisTemplate.delete(getRefreshTokenKey(adminId));
    }

    private String getRefreshTokenKey(Long adminId) {
        return "admin:refresh:" + adminId;
    }

}
