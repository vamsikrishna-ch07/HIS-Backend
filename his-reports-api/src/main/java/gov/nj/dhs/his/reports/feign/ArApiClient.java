package gov.nj.dhs.his.reports.feign;

import gov.nj.dhs.his.common.ApiResponse;
import gov.nj.dhs.his.reports.model.CitizenApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ArApiClient {

    private final WebClient webClient;

    public ArApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("lb://his-ar-api").build();
    }

    public Mono<ApiResponse<CitizenApplication>> getApplicationById(Long appId) {
        return webClient.get()
                .uri("/ar/application/{appId}", appId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CitizenApplication>>() {});
    }
}
