package gov.nj.dhs.his.bi.feign;

import gov.nj.dhs.his.bi.model.EligibilityDetails;
import gov.nj.dhs.his.common.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@FeignClient(name = "his-ed-api")
public interface EdApiClient {

    @GetMapping("/ed/eligibility/approved")
    @CircuitBreaker(name = "edApiCB", fallbackMethod = "getApprovedEligibilityFallback")
    @Retry(name = "edApiCB")
    ApiResponse<List<EligibilityDetails>> getApprovedEligibility();

    default ApiResponse<List<EligibilityDetails>> getApprovedEligibilityFallback(Throwable t) {
        return ApiResponse.<List<EligibilityDetails>>builder()
                .status("ERROR")
                .message("Service unavailable: Unable to fetch approved eligibility details. Please try again later.")
                .data(Collections.emptyList())
                .build();
    }
}
