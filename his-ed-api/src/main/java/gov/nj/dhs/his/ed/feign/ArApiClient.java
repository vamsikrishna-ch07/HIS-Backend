package gov.nj.dhs.his.ed.feign;

import gov.nj.dhs.his.common.ApiResponse;
import gov.nj.dhs.his.ed.config.FeignClientInterceptor;
import gov.nj.dhs.his.ed.model.Citizen;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "his-ar-api", configuration = FeignClientInterceptor.class)
public interface ArApiClient {

    @GetMapping("/ar/application/{appId}")
    ApiResponse<Citizen> getCitizen(@PathVariable("appId") Long appId);
}
