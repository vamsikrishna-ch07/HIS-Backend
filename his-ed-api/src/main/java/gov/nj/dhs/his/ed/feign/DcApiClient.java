package gov.nj.dhs.his.ed.feign;

import gov.nj.dhs.his.common.ApiResponse;
import gov.nj.dhs.his.ed.config.FeignClientInterceptor;
import gov.nj.dhs.his.ed.model.DcSummary;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "his-dc-api", configuration = FeignClientInterceptor.class)
public interface DcApiClient {

    @GetMapping("/dc/summary/{appId}")
    @CircuitBreaker(name = "dcApiCB", fallbackMethod = "getDcSummaryFallback")
    @Retry(name = "dcApiCB")
    ApiResponse<DcSummary> getDcSummary(@PathVariable("appId") Long appId);

    default ApiResponse<DcSummary> getDcSummaryFallback(Long appId, Throwable t) {
        return ApiResponse.<DcSummary>builder()
                .status("ERROR")
                .message("Service unavailable: Unable to fetch DC summary. Please try again later.")
                .data(null)
                .build();
    }
}
