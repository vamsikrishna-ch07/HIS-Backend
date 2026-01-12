package gov.nj.dhs.his.dc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nj.dhs.his.dc.config.SecurityConfig;
import gov.nj.dhs.his.dc.entity.Education;
import gov.nj.dhs.his.dc.entity.Income;
import gov.nj.dhs.his.dc.entity.Kid;
import gov.nj.dhs.his.dc.model.DcSummary;
import gov.nj.dhs.his.dc.service.DcService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DcController.class)
@Import(SecurityConfig.class)
class DcControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DcService dcService;

    @Autowired
    private ObjectMapper objectMapper;

    private DcSummary dcSummary;

    @BeforeEach
    void setUp() {
        Income income = Income.builder()
                .salaryIncome(5000.0)
                .rentIncome(1000.0)
                .propertyIncome(0.0)
                .build();

        Education education = new Education();
        education.setHighestDegree("Bachelors");
        education.setGraduationYear(2020);

        List<Kid> kids = new ArrayList<>();
        Kid kid1 = new Kid();
        kid1.setKidName("Kid1");
        kid1.setKidAge(5);
        kids.add(kid1);

        dcSummary = new DcSummary();
        dcSummary.setAppId(101L);
        dcSummary.setIncome(income);
        dcSummary.setEducation(education);
        dcSummary.setKids(kids);
    }

    @Test
    @WithMockUser(username = "caseworker", roles = {"CASEWORKER"})
    void saveDcSummary_ShouldReturnCreated() throws Exception {
        // Arrange
        when(dcService.saveSummary(any(DcSummary.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/dc/summary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dcSummary)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Data collection summary saved successfully"));
    }

    @Test
    @WithMockUser(username = "caseworker", roles = {"CASEWORKER"})
    void getDcSummary_ShouldReturnSummary_WhenFound() throws Exception {
        // Arrange
        when(dcService.getSummary(101L)).thenReturn(dcSummary);

        // Act & Assert
        mockMvc.perform(get("/dc/summary/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.appId").value(101));
    }

    @Test
    @WithMockUser(username = "caseworker", roles = {"CASEWORKER"})
    void getDcSummary_ShouldReturnNotFound_WhenNotFound() throws Exception {
        // Arrange
        when(dcService.getSummary(999L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/dc/summary/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
    }
}
