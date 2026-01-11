package gov.nj.dhs.his.bi.controller;

import gov.nj.dhs.his.bi.config.SecurityConfig;
import gov.nj.dhs.his.bi.repository.BenefitIssuanceRepository;
import gov.nj.dhs.his.bi.repository.PendingBenefitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BiController.class)
@Import(SecurityConfig.class) // Import SecurityConfig to apply security settings
class BiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobLauncher jobLauncher;

    @MockBean
    private Job benefitIssuanceJob;

    // Mock all dependencies required by the Batch Configuration
    @MockBean
    private JobRepository jobRepository;

    @MockBean
    private PlatformTransactionManager transactionManager;

    @MockBean
    private PendingBenefitRepository pendingBenefitRepository;

    @MockBean
    private BenefitIssuanceRepository benefitIssuanceRepository;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void issueBenefits_ShouldReturnSuccess() throws Exception {
        // Arrange
        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/bi/issue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Benefit issuance job started successfully."));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void issueBenefits_ShouldReturnError_WhenJobFails() throws Exception {
        // Arrange
        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenThrow(new RuntimeException("Job failed"));

        // Act & Assert
        mockMvc.perform(get("/bi/issue"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Failed to start benefit issuance job."));
    }
}
