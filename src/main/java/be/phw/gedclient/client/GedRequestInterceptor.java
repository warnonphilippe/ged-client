package be.phw.gedclient.client;

import be.phw.gedclient.config.ApplicationProperties;
import be.phw.gedclient.security.oauth2.AuthorizationHeaderUtil;

import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class GedRequestInterceptor implements RequestInterceptor {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer";

    private AuthorizationCodeResourceDetails authorizationCodeResourceDetails;
    private ApplicationProperties applicationProperties;
    private RestTemplate restTemplate;
    private OAuth2ClientContext oAuth2ClientContext;

    public GedRequestInterceptor(AuthorizationCodeResourceDetails authorizationCodeResourceDetails,
            ApplicationProperties applicationProperties) {
        super();
        this.authorizationCodeResourceDetails = authorizationCodeResourceDetails;
        this.applicationProperties = applicationProperties;
        restTemplate = new RestTemplate();
        oAuth2ClientContext = new DefaultOAuth2ClientContext();
    }

    @Override
    public void apply(RequestTemplate template) {
        // demander un token à keycloak et le joindre à la request
        Optional<String> token = getToken();
        if (token.isPresent()) {
            template.header(AUTHORIZATION, String.format("%s %s", BEARER, token.get()));
        }
    }

    private Optional<String> getToken() {

        if (oAuth2ClientContext.getAccessToken() == null) {
            //pas de token, on en demande un selon user/password
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", authorizationCodeResourceDetails.getClientId());
            map.add("client_secret", authorizationCodeResourceDetails.getClientSecret());
            map.add("grant_type", "password"); // client_credentials
            map.add("username", applicationProperties.getGedUser());
            map.add("password", applicationProperties.getGedPassword());
            oAuth2ClientContext.setAccessToken(askToken(map));

        } else if (oAuth2ClientContext.getAccessToken().isExpired()) {
            //token présent mais expiré, on utilise le refresh_token
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", authorizationCodeResourceDetails.getClientId());
            map.add("client_secret", authorizationCodeResourceDetails.getClientSecret());
            map.add("grant_type", "refresh_token"); 
            map.add("refresh_token", oAuth2ClientContext.getAccessToken().getRefreshToken().getValue());
            oAuth2ClientContext.setAccessToken(askToken(map));
        } 

        if (oAuth2ClientContext.getAccessToken() != null){
            return Optional.ofNullable(oAuth2ClientContext.getAccessToken().getValue());
        } else {
            return Optional.empty();
        }
        
    }

    private OAuth2AccessToken askToken( MultiValueMap<String, String> map) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<OAuth2AccessToken> response = restTemplate.postForEntity(
                    this.authorizationCodeResourceDetails.getAccessTokenUri(), request, OAuth2AccessToken.class);
            if (response != null && response.hasBody()) {
                return response.getBody();
            } else {
                return null;
            }
    }

}
