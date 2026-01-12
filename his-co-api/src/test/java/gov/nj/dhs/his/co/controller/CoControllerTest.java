package gov.nj.dhs.his.co.controller;

import gov.nj.dhs.his.co.config.ReactiveSecurityConfig;
import gov.nj.dhs.his.co.entity.CoTrigger;
import gov.nj.dhs.his.co.service.CoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@WebFluxTest(CoController.class)
@Import(ReactiveSecurityConfig.class)
class CoControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CoService coService;

    @Test
    @WithMockUser(username = "caseworker", roles = {"CASEWORKER"})
    void generateCorrespondence_ShouldReturnSuccess() {
        // Arrange
        CoTrigger trigger = CoTrigger.builder()
                .appId(101L)
                .triggerStatus("COMPLETED")
                .build();

        when(coService.processCorrespondence(101L)).thenReturn(Mono.just(trigger));

        // Act & Assert
        webTestClient.get().uri("/co/generate/101")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("SUCCESS")
                .jsonPath("$.data.triggerStatus").isEqualTo("COMPLETED");
    }
}
