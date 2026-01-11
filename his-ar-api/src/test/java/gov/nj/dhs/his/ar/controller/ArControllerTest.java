package gov.nj.dhs.his.ar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nj.dhs.his.ar.config.SecurityConfig;
import gov.nj.dhs.his.ar.entity.CitizenApplication;
import gov.nj.dhs.his.ar.service.ArService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArController.class)
@Import(SecurityConfig.class)
class ArControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArService arService;

    @Autowired
    private ObjectMapper objectMapper;

    private CitizenApplication validApplication;

    @BeforeEach
    void setUp() {
        validApplication = CitizenApplication.builder()
                .appId(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .phNo(1234567890L)
                .gender("Male")
                .ssn(123456789L)
                .stateName("New Jersey")
                .build();
    }

    @Test
    @WithMockUser(username = "caseworker", roles = {"CASEWORKER"})
    void createApplication_ShouldReturnCreated() throws Exception {
        // Arrange
        when(arService.createApplication(any(CitizenApplication.class))).thenReturn(validApplication);

        // Act & Assert
        mockMvc.perform(post("/ar/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validApplication)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.appId").value(1));
    }

    @Test
    @WithMockUser(username = "caseworker", roles = {"CASEWORKER"})
    void getApplicationById_ShouldReturnApplication_WhenFound() throws Exception {
        // Arrange
        when(arService.getApplicationById(1L)).thenReturn(Optional.of(validApplication));

        // Act & Assert
        mockMvc.perform(get("/ar/application/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.fullName").value("John Doe"));
    }

    @Test
    @WithMockUser(username = "caseworker", roles = {"CASEWORKER"})
    void getApplicationById_ShouldReturnNotFound_WhenNotFound() throws Exception {
        // Arrange
        when(arService.getApplicationById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/ar/application/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
    }
}
