package gov.nj.dhs.his.apigateway.config;

import gov.nj.dhs.his.common.config.BaseWebFluxSecurityConfig;
import gov.nj.dhs.his.common.security.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@Import(BaseWebFluxSecurityConfig.class)
public class ReactiveSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveSecurityConfig.class);

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        logger.info("Loading ReactiveSecurityConfig for API Gateway");

        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .headers(headers -> headers.frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(SecurityConstants.SWAGGER_WHITELIST).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new BaseWebFluxSecurityConfig().reactiveJwtAuthenticationConverter()))
                );

        return http.build();
    }
}
