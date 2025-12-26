package gov.nj.dhs.his.ed.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignClientInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(FeignClientInterceptor.class);

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            logger.info("Feign Interceptor: Auth object: {}", authentication);

            if (authentication != null && authentication instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;
                String tokenValue = jwtToken.getToken().getTokenValue();
                template.header("Authorization", "Bearer " + tokenValue);
                logger.info("Feign Interceptor: Added Authorization header");
            } else {
                logger.warn("Feign Interceptor: No JWT found");
            }
        };
    }
}
