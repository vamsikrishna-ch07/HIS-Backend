package gov.nj.dhs.his.co.service;

import gov.nj.dhs.his.common.ApiResponse;
import gov.nj.dhs.his.co.entity.CoTrigger;
import gov.nj.dhs.his.co.feign.EdApiClient;
import gov.nj.dhs.his.co.model.EligibilityDetails;
import gov.nj.dhs.his.co.repository.CoTriggerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoServiceImplTest {

    @Mock
    private EdApiClient edApiClient;

    @Mock
    private CoTriggerRepository coTriggerRepository;

    @InjectMocks
    private CoServiceImpl coService;

    private EligibilityDetails eligibilityDetails;
    private CoTrigger coTrigger;

    @BeforeEach
    void setUp() {
        eligibilityDetails = new EligibilityDetails();
        eligibilityDetails.setAppId(101L);
        eligibilityDetails.setPlanName("SNAP");
        eligibilityDetails.setPlanStatus("APPROVED");
        eligibilityDetails.setBenefitAmount(250.00);
        eligibilityDetails.setStartDate(LocalDate.now());
        eligibilityDetails.setEndDate(LocalDate.now().plusYears(1));

        coTrigger = CoTrigger.builder()
                .triggerId(1L)
                .appId(101L)
                .triggerStatus("COMPLETED")
                .build();
    }

    @Test
    void processCorrespondence_ShouldGenerateNotice_WhenEligibilityFound() {
        // Arrange
        when(edApiClient.getEligibilityDetails(101L))
                .thenReturn(Mono.just(ApiResponse.<EligibilityDetails>builder().data(eligibilityDetails).build()));
        when(coTriggerRepository.save(any(CoTrigger.class))).thenReturn(Mono.just(coTrigger));

        // Act
        Mono<CoTrigger> result = coService.processCorrespondence(101L);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(trigger -> "COMPLETED".equals(trigger.getTriggerStatus()))
                .verifyComplete();
    }

    @Test
    void processCorrespondence_ShouldFail_WhenEligibilityNotFound() {
        // Arrange
        when(edApiClient.getEligibilityDetails(999L))
                .thenReturn(Mono.just(ApiResponse.<EligibilityDetails>builder().data(null).build()));
        
        CoTrigger failedTrigger = CoTrigger.builder()
                .appId(999L)
                .triggerStatus("FAILED")
                .failureReason("Eligibility details not found.")
                .build();
        
        when(coTriggerRepository.save(any(CoTrigger.class))).thenReturn(Mono.just(failedTrigger));

        // Act
        Mono<CoTrigger> result = coService.processCorrespondence(999L);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(trigger -> "FAILED".equals(trigger.getTriggerStatus()))
                .verifyComplete();
    }

    @Test
    void generateCorrespondence_ShouldSaveKafkaTrigger() {
        // Arrange
        CoTrigger kafkaTrigger = CoTrigger.builder()
                .appId(101L)
                .triggerStatus("COMPLETED_FROM_KAFKA")
                .build();
        
        when(coTriggerRepository.save(any(CoTrigger.class))).thenReturn(Mono.just(kafkaTrigger));

        // Act
        Mono<Void> result = coService.generateCorrespondence(101L, "APPROVED");

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }
}
