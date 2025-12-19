package gov.nj.dhs.his.admin.controller;

import gov.nj.dhs.his.admin.entity.InsurancePlan;
import gov.nj.dhs.his.admin.service.AdminService;
import gov.nj.dhs.his.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/plan")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InsurancePlan>> createPlan(@RequestBody InsurancePlan plan) {
        InsurancePlan savedPlan = adminService.savePlan(plan);
        ApiResponse<InsurancePlan> response = ApiResponse.<InsurancePlan>builder()
                .status("SUCCESS")
                .message("Plan created successfully")
                .data(savedPlan)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/plans")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASE_WORKER')")
    public ResponseEntity<ApiResponse<List<InsurancePlan>>> getAllPlans() {
        List<InsurancePlan> plans = adminService.getAllPlans();
        ApiResponse<List<InsurancePlan>> response = ApiResponse.<List<InsurancePlan>>builder()
                .status("SUCCESS")
                .message("All plans retrieved successfully")
                .data(plans)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/plan/{planId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASE_WORKER')")
    public ResponseEntity<ApiResponse<InsurancePlan>> getPlanById(@PathVariable Long planId) {
        Optional<InsurancePlan> planOpt = adminService.getPlanById(planId);
        if (planOpt.isPresent()) {
            ApiResponse<InsurancePlan> response = ApiResponse.<InsurancePlan>builder()
                    .status("SUCCESS")
                    .message("Plan retrieved successfully")
                    .data(planOpt.get())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<InsurancePlan> response = ApiResponse.<InsurancePlan>builder()
                    .status("NOT_FOUND")
                    .message("Plan not found with ID: " + planId)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/plan/{planId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InsurancePlan>> updatePlan(@PathVariable Long planId, @RequestBody InsurancePlan planDetails) {
        InsurancePlan updatedPlan = adminService.updatePlan(planId, planDetails);
        if (updatedPlan != null) {
            ApiResponse<InsurancePlan> response = ApiResponse.<InsurancePlan>builder()
                    .status("SUCCESS")
                    .message("Plan updated successfully")
                    .data(updatedPlan)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<InsurancePlan> response = ApiResponse.<InsurancePlan>builder()
                    .status("NOT_FOUND")
                    .message("Plan not found with ID: " + planId)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/plan/{planId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivatePlan(@PathVariable Long planId) {
        boolean isDeactivated = adminService.deactivatePlan(planId);
        if (isDeactivated) {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .status("SUCCESS")
                    .message("Plan deactivated successfully")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .status("NOT_FOUND")
                    .message("Plan not found with ID: " + planId)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}