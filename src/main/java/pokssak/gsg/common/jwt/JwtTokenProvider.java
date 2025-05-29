package pokssak.gsg.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import pokssak.gsg.common.exception.CustomException;

@Component
public class JwtTokenProvider {

    private final long tokenExpireSeconds;
    private final Key key;
    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.token-expire-seconds}") long tokenExpireSeconds,
                            UserDetailsService userDetailsService) {
        this.tokenExpireSeconds = tokenExpireSeconds;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userDetailsService = userDetailsService;
    }


    public JwtTokenDto createToken(Long userId) {
        long now = (new Date()).getTime();
        Date accessTokenExpireTime = new Date(now + tokenExpireSeconds);
        Date refreshTokenExpireTime = new Date(now + (tokenExpireSeconds * 2 * 30));

        String accessToken = Jwts.builder()
            .setSubject(String.valueOf(userId))
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(accessTokenExpireTime)
            .compact();

        String refreshToken = Jwts.builder()
            .setSubject(String.valueOf(userId))
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(refreshTokenExpireTime)
            .compact();

        return JwtTokenDto.of(accessToken, refreshToken);
    }

    public Authentication getAuthentication(String accessToken) {
        String userId = getClaims(accessToken).getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
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
}
