package be.phw.gedclient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

import java.io.Serializable;
import java.util.Optional;

/**
 * Extension de AuthorizationCodeResourceDetails
 */
@ConfigurationProperties(prefix = "security.oauth2.client")
public class MyAuthorizationCodeResourceDetails extends AuthorizationCodeResourceDetails implements Serializable {
    
}
