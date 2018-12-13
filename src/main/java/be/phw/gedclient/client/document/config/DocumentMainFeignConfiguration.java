package be.phw.gedclient.client.document.config;

import feign.Contract;
import feign.form.FormEncoder;
import feign.gson.GsonEncoder;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;

public class DocumentMainFeignConfiguration { // extends OAuth2InterceptedFeignConfiguration

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

}
