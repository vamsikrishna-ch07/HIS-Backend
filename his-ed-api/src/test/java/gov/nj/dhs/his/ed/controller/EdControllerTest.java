package gov.nj.dhs.his.ed.controller;

import gov.nj.dhs.his.ed.config.SecurityConfig;
import gov.nj.dhs.his.ed.entity.EligibilityDetails;
import gov.nj.dhs.his.ed.service.EdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EdController.class)
@Import(SecurityConfig.class)
class EdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EdService edService;

    private EligibilityDetails eligibilityDetails;

    @BeforeEach
    void setUp() {
        eligibilityDetails = EligibilityDetails.builder()
                .eligId(1L)
                .appId(101L)
                .planName("SNAP")
                .planStatus("APPROVED")
                .benefitAmount(250.00)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .build();
    }

    @Test
    @WithMockUser(username = "caseworker", roles = {"CASEWORKER"})
    void determineEligibility_ShouldReturnEligibilityDetails() throws Exception {
        // Arrange
        when(edService.determineEligibility(101L)).thenReturn(eligibilityDetails);

        // Act & Assert
        mockMvc.perform(get("/ed/determine/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.planName").value("SNAP"))
                .andExpect(jsonPath("$.data.planStatus").value("APPROVED"));
    }

    @Test
    @WithMockUser(username = "caseworker", roles = {"CASEWORKER"})
    void getEligibility_ShouldReturnDetails_WhenFound() throws Exception {
        // Arrange
        when(edService.getEligibility(101L)).thenReturn(Optional.of(eligibilityDetails));

        // Act & Assert
        mockMvc.perform(get("/ed/eligibility/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.appId").value(101));
    }

    @Test
    @WithMockUser(username = "caseworker", roles = {"CASEWORKER"})
    void getEligibility_ShouldReturnNotFound_WhenNotFound() throws Exception {
        // Arrange
        when(edService.getEligibility(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/ed/eligibility/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
    }
}
