package be.phw.gedclient.client.document.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

import be.phw.gedclient.config.ApplicationProperties;
import feign.Logger;
import feign.RequestInterceptor;

public class AuthClientConfiguration {

    private final AuthorizationCodeResourceDetails authorizationCodeResourceDetails;
    private final ApplicationProperties applicationProperties;

    public AuthClientConfiguration(AuthorizationCodeResourceDetails authorizationCodeResourceDetails, ApplicationProperties applicationProperties) {
        this.authorizationCodeResourceDetails = authorizationCodeResourceDetails;
        this.applicationProperties = applicationProperties;
    }
    
    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor() {
       return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), resource());
    }
  
    @Bean
    public Logger.Level feignLoggerLevel() {
       return Logger.Level.FULL;
    }
  
    private OAuth2ProtectedResourceDetails resource() {
       ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
       resourceDetails.setUsername(applicationProperties.getGedUser());
       resourceDetails.setPassword(applicationProperties.getGedPassword());
       resourceDetails.setAccessTokenUri(authorizationCodeResourceDetails.getAccessTokenUri());
       resourceDetails.setClientId(authorizationCodeResourceDetails.getClientId());
       resourceDetails.setClientSecret(authorizationCodeResourceDetails.getClientSecret());
       resourceDetails.setGrantType("password");
       resourceDetails.setScope(authorizationCodeResourceDetails.getScope());
       return resourceDetails;
    }
  
 }
 