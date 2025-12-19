package gov.nj.dhs.his.bi.feign;

import gov.nj.dhs.his.bi.model.EligibilityDetails;
import gov.nj.dhs.his.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "his-ed-api")
public interface EdApiClient {

    @GetMapping("/ed/eligibility/approved")
    ApiResponse<List<EligibilityDetails>> getApprovedEligibility();

}