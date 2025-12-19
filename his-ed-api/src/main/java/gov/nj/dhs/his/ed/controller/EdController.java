package gov.nj.dhs.his.ed.controller;

import gov.nj.dhs.his.common.ApiResponse;
import gov.nj.dhs.his.ed.entity.EligibilityDetails;
import gov.nj.dhs.his.ed.service.EdService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ed")
public class EdController {

    private final EdService edService;

    public EdController(EdService edService) {
        this.edService = edService;
    }

    @GetMapping("/determine/{appId}")
    public ResponseEntity<ApiResponse<EligibilityDetails>> determineEligibility(@PathVariable Long appId) {
        EligibilityDetails eligibilityDetails = edService.determineEligibility(appId);
        ApiResponse<EligibilityDetails> response = ApiResponse.<EligibilityDetails>builder()
                .status("SUCCESS")
                .message("Eligibility determination complete.")
                .data(eligibilityDetails)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/eligibility/{appId}")
    public ResponseEntity<ApiResponse<EligibilityDetails>> getEligibility(@PathVariable Long appId) {
        Optional<EligibilityDetails> eligibilityOpt = edService.getEligibility(appId);
        if (eligibilityOpt.isPresent()) {
            ApiResponse<EligibilityDetails> response = ApiResponse.<EligibilityDetails>builder()
                    .status("SUCCESS")
                    .message("Eligibility details retrieved successfully.")
                    .data(eligibilityOpt.get())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<EligibilityDetails> response = ApiResponse.<EligibilityDetails>builder()
                    .status("NOT_FOUND")
                    .message("Eligibility details not found for App ID: " + appId)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/eligibility/approved")
    public ResponseEntity<ApiResponse<List<EligibilityDetails>>> getApprovedEligibility() {
        List<EligibilityDetails> approvedList = edService.getApprovedEligibility();
        ApiResponse<List<EligibilityDetails>> response = ApiResponse.<List<EligibilityDetails>>builder()
                .status("SUCCESS")
                .message("Approved eligibility details retrieved successfully.")
                .data(approvedList)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}