package gov.nj.dhs.his.bi.config;

import gov.nj.dhs.his.common.config.BaseMvcSecurityConfig;
import gov.nj.dhs.his.common.security.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Import(BaseMvcSecurityConfig.class)
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Loading SecurityConfig for Servlet Application (Resource Server)");

        // Explicitly use AntPathRequestMatcher to resolve ambiguity
        AntPathRequestMatcher[] whitelistMatchers = Arrays.stream(SecurityConstants.SWAGGER_WHITELIST)
                .map(AntPathRequestMatcher::new)
                .toArray(AntPathRequestMatcher[]::new);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(whitelistMatchers).permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new BaseMvcSecurityConfig().jwtAuthenticationConverter()))
                );
        return http.build();

    }
}
