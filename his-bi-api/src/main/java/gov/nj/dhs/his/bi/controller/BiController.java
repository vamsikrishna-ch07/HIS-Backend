package gov.nj.dhs.his.bi.controller;

import gov.nj.dhs.his.common.ApiResponse;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bi")
public class BiController {

    private final JobLauncher jobLauncher;
    private final Job benefitIssuanceJob;

    public BiController(JobLauncher jobLauncher, Job benefitIssuanceJob) {
        this.jobLauncher = jobLauncher;
        this.benefitIssuanceJob = benefitIssuanceJob;
    }

    @GetMapping("/issue")
    public ResponseEntity<ApiResponse<String>> issueBenefits() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(benefitIssuanceJob, jobParameters);
            return new ResponseEntity<>(
                    ApiResponse.<String>builder()
                            .status("SUCCESS")
                            .message("Benefit issuance job started successfully.")
                            .build(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    ApiResponse.<String>builder()
                            .status("ERROR")
                            .message("Failed to start benefit issuance job.")
                            .errors(List.of(e.getMessage()))
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}