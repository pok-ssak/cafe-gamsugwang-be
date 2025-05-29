package pokssak.gsg.domain.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pokssak.gsg.common.config.GoogleConfig;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.common.exception.GlobalErrorCode;
import pokssak.gsg.domain.user.dto.OAuthCodeDto;
import pokssak.gsg.domain.user.dto.UserInfoResponseDto;
import pokssak.gsg.domain.user.entity.JoinType;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class GoogleService implements OAuthService {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final GoogleConfig googleConfig;

    @Override
    public String getAccessToken(OAuthCodeDto oAuthCodeDto) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();

        param.add("grant_type", "authorization_code");
        param.add("client_id", googleConfig.getClientId());
        param.add("client_secret", googleConfig.getClientSecret());
        param.add("redirect_uri", googleConfig.getRedirectUri());
        param.add("code", oAuthCodeDto.code());

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(param, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                googleConfig.getOAuthTokenUri(),
                HttpMethod.POST,
                httpEntity,
                String.class);

            JsonNode rootNode = objectMapper.readTree(response.getBody());
            return rootNode.path("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(GlobalErrorCode.INTERNAL_SERVER);
        }
    }

    @Override
    public UserInfoResponseDto getUserInfo(String oAuthAccessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(oAuthAccessToken);

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                googleConfig.getUserInfoUri(),
                HttpMethod.GET,
                httpEntity,
                String.class);

            JsonNode rootNode = objectMapper.readTree(response.getBody());

            String id = rootNode.path("id").asText();

            return new UserInfoResponseDto(id);
        } catch (Exception e) {
            throw new CustomException(GlobalErrorCode.INTERNAL_SERVER);
        }
    }

    @Override
    public User getOAuthUser(String oauthPlatformId) {
        return userRepository.findByOauthPlatformIdAndJoinType(oauthPlatformId, JoinType.GOOGLE)
            .orElseGet(() -> createOAuthUser(oauthPlatformId));
    }

    private User createOAuthUser(String platformId) {

        return userRepository.save(User.builder()
            .oauthPlatformId(platformId)
            .joinType(JoinType.KAKAO)
            .build()
        );
    }
}
