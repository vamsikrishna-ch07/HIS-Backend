package gov.nj.dhs.his.bi.service;

import gov.nj.dhs.his.bi.entity.PendingBenefit;
import gov.nj.dhs.his.bi.model.EligibilityEvent;
import gov.nj.dhs.his.bi.repository.PendingBenefitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BenefitServiceImplTest {

    @Mock
    private PendingBenefitRepository pendingBenefitRepository;

    @InjectMocks
    private BenefitServiceImpl benefitService;

    private EligibilityEvent eligibilityEvent;

    @BeforeEach
    void setUp() {
        eligibilityEvent = new EligibilityEvent();
        eligibilityEvent.setApplicationId(123L);
        eligibilityEvent.setCitizenId(456L);
        eligibilityEvent.setPlanName("SNAP");
        eligibilityEvent.setBenefitAmount(200.00);
    }

    @Test
    void savePendingBenefit_ShouldSaveBenefit() {
        // Act
        benefitService.savePendingBenefit(eligibilityEvent);

        // Assert
        verify(pendingBenefitRepository, times(1)).save(any(PendingBenefit.class));
    }
}
