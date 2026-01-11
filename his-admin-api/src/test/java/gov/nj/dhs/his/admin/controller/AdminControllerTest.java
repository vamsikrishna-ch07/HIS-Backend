package gov.nj.dhs.his.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nj.dhs.his.admin.config.SecurityConfig;
import gov.nj.dhs.his.admin.entity.InsurancePlan;
import gov.nj.dhs.his.admin.entity.PlanStatus;
import gov.nj.dhs.his.admin.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    private InsurancePlan plan;

    @BeforeEach
    void setUp() {
        plan = InsurancePlan.builder()
                .planId(1L)
                .planName("SNAP")
                .planStatus(PlanStatus.ACTIVE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createPlan_ShouldReturnCreated_ForAdmin() throws Exception {
        // Arrange
        when(adminService.savePlan(any(InsurancePlan.class))).thenReturn(plan);

        // Act & Assert
        mockMvc.perform(post("/admin/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plan)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.planName").value("SNAP"));
    }

    @Test
    @WithMockUser(username = "caseworker", roles = {"CASEWORKER"})
    void createPlan_ShouldReturnForbidden_ForCaseworker() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/admin/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plan)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deactivatePlan_ShouldReturnOk_ForAdmin() throws Exception {
        // Arrange
        when(adminService.deactivatePlan(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/admin/plan/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Plan deactivated successfully"));
    }

    @Test
    @WithMockUser(username = "caseworker", roles = {"CASEWORKER"})
    void deactivatePlan_ShouldReturnForbidden_ForCaseworker() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/admin/plan/1"))
                .andExpect(status().isForbidden());
    }
}
