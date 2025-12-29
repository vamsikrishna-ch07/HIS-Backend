package gov.nj.dhs.his.reports.feign;

import gov.nj.dhs.his.common.ApiResponse;
import gov.nj.dhs.his.reports.model.EligibilityDetails;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class EdApiClient {

    private final WebClient webClient;

    public EdApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("lb://his-ed-api").build();
    }

    @CircuitBreaker(name = "edApiCB", fallbackMethod = "getEligibilityDetailsFallback")
    @TimeLimiter(name = "edApiCB")
    @Retry(name = "edApiCB")
    @Bulkhead(name = "edApiCB")
    public Mono<ApiResponse<EligibilityDetails>> getEligibilityDetails(Long appId) {
        return webClient.get()
                .uri("/ed/eligibility/{appId}", appId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<EligibilityDetails>>() {});
    }

    public Mono<ApiResponse<EligibilityDetails>> getEligibilityDetailsFallback(Long appId, Throwable t) {
        return Mono.just(ApiResponse.<EligibilityDetails>builder()
                .status("ERROR")
                .message("Service unavailable: Unable to fetch eligibility details. Please try again later.")
                .data(null)
                .build());
    }
}
