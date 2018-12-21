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
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class GedRequestInterceptor implements RequestInterceptor {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer";

    private AuthorizationCodeResourceDetails authorizationCodeResourceDetails;
    private ApplicationProperties applicationProperties;
    private RestTemplate restTemplate;

    public GedRequestInterceptor(AuthorizationCodeResourceDetails authorizationCodeResourceDetails, ApplicationProperties applicationProperties) {
        super();
        this.authorizationCodeResourceDetails = authorizationCodeResourceDetails;
        this.applicationProperties = applicationProperties;
        restTemplate = new RestTemplate();
    }

    @Override
    public void apply(RequestTemplate template) {
        //demander un token à keycloak et le joindre à la request
        Optional<String> token = getToken();
        if (token.isPresent()){
            template.header(AUTHORIZATION, String.format("%s %s", BEARER, token.get()));
        }
    }

    private Optional<String> getToken(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("username", applicationProperties.getGedUser());
        map.add("password", applicationProperties.getGedPasword());
        map.add("client_id", authorizationCodeResourceDetails.getClientId());
        map.add("grant_type", "password");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(this.authorizationCodeResourceDetails.getAccessTokenUri(), request , String.class);
        //TODO : extraire le token
        System.out.println(response.getBody());
        return Optional.empty();

    }
}
