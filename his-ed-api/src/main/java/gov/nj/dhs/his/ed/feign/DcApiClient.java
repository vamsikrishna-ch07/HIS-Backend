package gov.nj.dhs.his.ed.feign;

import gov.nj.dhs.his.common.ApiResponse;
import gov.nj.dhs.his.ed.model.DcSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "his-dc-api")
public interface DcApiClient {

    @GetMapping("/dc/summary/{appId}")
    ApiResponse<DcSummary> getDcSummary(@PathVariable("appId") Long appId);

}