package gov.nj.dhs.his.apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class SwaggerRedirectController {

    @Bean
    public RouterFunction<ServerResponse> swaggerRouter() {
        return route(GET("/swagger-ui.html"), req ->
                ServerResponse.temporaryRedirect(URI.create("/webjars/swagger-ui/index.html")).build())
                .andRoute(GET("/swagger-ui/index.html"), req ->
                        ServerResponse.temporaryRedirect(URI.create("/webjars/swagger-ui/index.html")).build())
                .andRoute(GET("/swagger-ui"), req ->
                        ServerResponse.temporaryRedirect(URI.create("/webjars/swagger-ui/index.html")).build());
    }
}
