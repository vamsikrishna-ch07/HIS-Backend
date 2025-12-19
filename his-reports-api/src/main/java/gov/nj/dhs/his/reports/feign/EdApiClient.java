package gov.nj.dhs.his.reports.feign;

import gov.nj.dhs.his.common.ApiResponse;
import gov.nj.dhs.his.reports.model.EligibilityDetails;
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

    public Mono<ApiResponse<EligibilityDetails>> getEligibilityDetails(Long appId) {
        return webClient.get()
                .uri("/ed/eligibility/{appId}", appId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<EligibilityDetails>>() {});
    }
}
