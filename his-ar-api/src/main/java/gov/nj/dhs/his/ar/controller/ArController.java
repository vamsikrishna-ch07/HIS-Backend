package gov.nj.dhs.his.ar.controller;

import gov.nj.dhs.his.ar.entity.CitizenApplication;
import gov.nj.dhs.his.ar.service.ArService;
import gov.nj.dhs.his.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/ar")
public class ArController {

    private final ArService arService;

    public ArController(ArService arService) {
        this.arService = arService;
    }

    @PostMapping("/application")
    public ResponseEntity<ApiResponse<CitizenApplication>> createApplication(@RequestBody CitizenApplication application) {
        CitizenApplication createdApplication = arService.createApplication(application);
        ApiResponse<CitizenApplication> response = ApiResponse.<CitizenApplication>builder()
                .status("SUCCESS")
                .message("Application created successfully")
                .data(createdApplication)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/application/{appId}")
    public ResponseEntity<ApiResponse<CitizenApplication>> getApplicationById(@PathVariable Long appId) {
        Optional<CitizenApplication> appOpt = arService.getApplicationById(appId);
        if (appOpt.isPresent()) {
            ApiResponse<CitizenApplication> response = ApiResponse.<CitizenApplication>builder()
                    .status("SUCCESS")
                    .message("Application retrieved successfully")
                    .data(appOpt.get())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<CitizenApplication> response = ApiResponse.<CitizenApplication>builder()
                    .status("NOT_FOUND")
                    .message("Application not found with ID: " + appId)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}