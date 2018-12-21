package be.phw.gedclient.client.document.config;

import feign.Contract;
import feign.RequestInterceptor;
import feign.form.FormEncoder;
import feign.gson.GsonEncoder;

import java.io.IOException;

import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

import be.phw.gedclient.client.GedRequestInterceptor;
import be.phw.gedclient.config.ApplicationProperties;
import be.phw.gedclient.security.oauth2.AuthorizationHeaderUtil;

public class DocumentMainFeignConfiguration {

    @Bean
    public Contract feignContract() {
        // return new SpringMvcContract();
        return new feign.Contract.Default();
    }

    @Bean
    public feign.codec.Encoder feignEncoder() {
        //return new GsonEncoder();
        return new FormEncoder(new GsonEncoder());
    }

    @Bean
    public feign.codec.Decoder feignDecoder() {
        //return new feign.codec.Decoder.Default();
        return new ResponseEntityDecoder(new CustomFileDecoder(new CustomGsonDecoder()));
    }

    /*
    @Bean(name = "gedRequestInterceptor")
    public RequestInterceptor getGedRequestInterceptor(AuthorizationCodeResourceDetails authorizationCodeResourceDetails, ApplicationProperties applicationProperties) throws IOException {
        return new GedRequestInterceptor(authorizationCodeResourceDetails, applicationProperties);
    }
    */

}
