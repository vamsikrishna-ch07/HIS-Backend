package gov.nj.dhs.his.ed.feign;

import gov.nj.dhs.his.common.ApiResponse;
import gov.nj.dhs.his.ed.config.FeignClientInterceptor;
import gov.nj.dhs.his.ed.model.Citizen;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "his-ar-api", configuration = FeignClientInterceptor.class)
public interface ArApiClient {

    @GetMapping("/ar/application/{appId}")
    @CircuitBreaker(name = "arApiCB", fallbackMethod = "getCitizenFallback")
    @Retry(name = "arApiCB")
    ApiResponse<Citizen> getCitizen(@PathVariable("appId") Long appId);

    default ApiResponse<Citizen> getCitizenFallback(Long appId, Throwable t) {
        return ApiResponse.<Citizen>builder()
                .status("ERROR")
                .message("Service unavailable: Unable to fetch citizen details. Please try again later.")
                .data(null)
                .build();
    }
}
