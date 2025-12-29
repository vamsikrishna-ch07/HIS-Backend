package gov.nj.dhs.his.reports.feign;

import gov.nj.dhs.his.common.ApiResponse;
import gov.nj.dhs.his.reports.model.CitizenApplication;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
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

    @CircuitBreaker(name = "arApiCB", fallbackMethod = "getApplicationByIdFallback")
    @TimeLimiter(name = "arApiCB")
    @Retry(name = "arApiCB")
    @Bulkhead(name = "arApiCB")
    public Mono<ApiResponse<CitizenApplication>> getApplicationById(Long appId) {
        return webClient.get()
                .uri("/ar/application/{appId}", appId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CitizenApplication>>() {});
    }

    public Mono<ApiResponse<CitizenApplication>> getApplicationByIdFallback(Long appId, Throwable t) {
        return Mono.just(ApiResponse.<CitizenApplication>builder()
                .status("ERROR")
                .message("Service unavailable: Unable to fetch application details. Please try again later.")
                .data(null)
                .build());
    }
}
