package gov.nj.dhs.his.dc.controller;

import gov.nj.dhs.his.common.ApiResponse;
import gov.nj.dhs.his.dc.model.DcSummary;
import gov.nj.dhs.his.dc.service.DcService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dc")
public class DcController {

    private final DcService dcService;

    public DcController(DcService dcService) {
        this.dcService = dcService;
    }

    @PostMapping("/summary")
    public ResponseEntity<ApiResponse<String>> saveDcSummary(@RequestBody DcSummary summary) {
        boolean isSaved = dcService.saveSummary(summary);
        if (isSaved) {
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .status("SUCCESS")
                    .message("Data collection summary saved successfully")
                    .data("Case ID: " + summary.getAppId()) // Or a more meaningful response
                    .build();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .status("ERROR")
                    .message("Failed to save data collection summary")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/summary/{appId}")
    public ResponseEntity<ApiResponse<DcSummary>> getDcSummary(@PathVariable Long appId) {
        DcSummary summary = dcService.getSummary(appId);
        if (summary != null) {
            ApiResponse<DcSummary> response = ApiResponse.<DcSummary>builder()
                    .status("SUCCESS")
                    .message("Data collection summary retrieved successfully")
                    .data(summary)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<DcSummary> response = ApiResponse.<DcSummary>builder()
                    .status("NOT_FOUND")
                    .message("No summary found for App ID: " + appId)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}