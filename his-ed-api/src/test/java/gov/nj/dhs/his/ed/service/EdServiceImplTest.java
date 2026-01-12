package gov.nj.dhs.his.ed.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nj.dhs.his.common.ApiResponse;
import gov.nj.dhs.his.ed.entity.EligibilityDetails;
import gov.nj.dhs.his.ed.feign.ArApiClient;
import gov.nj.dhs.his.ed.feign.DcApiClient;
import gov.nj.dhs.his.ed.model.Citizen;
import gov.nj.dhs.his.ed.model.DcSummary;
import gov.nj.dhs.his.ed.model.Income;
import gov.nj.dhs.his.ed.model.Kid;
import gov.nj.dhs.his.ed.repository.EligibilityDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EdServiceImplTest {

    @Mock
    private DcApiClient dcApiClient;

    @Mock
    private ArApiClient arApiClient;

    @Mock
    private EligibilityDetailsRepository eligibilityRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EdServiceImpl edService;

    private DcSummary dcSummary;
    private Citizen citizen;

    @BeforeEach
    void setUp() {
        // Setup default Citizen (Age 30)
        citizen = new Citizen();
        citizen.setAppId(101L);
        citizen.setSsn(123456789L);
        citizen.setDob(LocalDate.now().minusYears(30));

        // Setup default Income (Total 1500)
        Income income = new Income();
        income.setSalaryIncome(1500.0);
        income.setRentIncome(0.0);
        income.setPropertyIncome(0.0);

        dcSummary = new DcSummary();
        dcSummary.setAppId(101L);
        dcSummary.setIncome(income);
        dcSummary.setKids(new ArrayList<>());
    }

    @Test
    void determineEligibility_ShouldApproveMedicare_WhenAgeIs65OrMore() {
        // Arrange
        citizen.setDob(LocalDate.now().minusYears(66)); // Age 66
        
        when(dcApiClient.getDcSummary(101L)).thenReturn(ApiResponse.<DcSummary>builder().data(dcSummary).build());
        when(arApiClient.getCitizen(101L)).thenReturn(ApiResponse.<Citizen>builder().data(citizen).build());
        when(eligibilityRepository.save(any(EligibilityDetails.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        EligibilityDetails result = edService.determineEligibility(101L);

        // Assert
        assertEquals("Medicare", result.getPlanName());
        assertEquals("APPROVED", result.getPlanStatus());
    }

    @Test
    void determineEligibility_ShouldApproveCCAP_WhenIncomeLowAndHasKids() {
        // Arrange
        dcSummary.getIncome().setSalaryIncome(2000.0); // Total 2000
        List<Kid> kids = new ArrayList<>();
        kids.add(new Kid());
        dcSummary.setKids(kids); // 1 Kid

        when(dcApiClient.getDcSummary(101L)).thenReturn(ApiResponse.<DcSummary>builder().data(dcSummary).build());
        when(arApiClient.getCitizen(101L)).thenReturn(ApiResponse.<Citizen>builder().data(citizen).build());
        when(eligibilityRepository.save(any(EligibilityDetails.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        EligibilityDetails result = edService.determineEligibility(101L);

        // Assert
        assertEquals("CCAP", result.getPlanName());
        assertEquals("APPROVED", result.getPlanStatus());
    }

    @Test
    void determineEligibility_ShouldApproveSNAP_WhenIncomeLowAndNoKids() {
        // Arrange
        dcSummary.getIncome().setSalaryIncome(1800.0); // Total 1800
        // No kids by default

        when(dcApiClient.getDcSummary(101L)).thenReturn(ApiResponse.<DcSummary>builder().data(dcSummary).build());
        when(arApiClient.getCitizen(101L)).thenReturn(ApiResponse.<Citizen>builder().data(citizen).build());
        when(eligibilityRepository.save(any(EligibilityDetails.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        EligibilityDetails result = edService.determineEligibility(101L);

        // Assert
        assertEquals("SNAP", result.getPlanName());
        assertEquals("APPROVED", result.getPlanStatus());
    }

    @Test
    void determineEligibility_ShouldApproveMedicaid_WhenIncomeMedium() {
        // Arrange
        dcSummary.getIncome().setSalaryIncome(2800.0); // Total 2800 (Between 2000 and 3000)

        when(dcApiClient.getDcSummary(101L)).thenReturn(ApiResponse.<DcSummary>builder().data(dcSummary).build());
        when(arApiClient.getCitizen(101L)).thenReturn(ApiResponse.<Citizen>builder().data(citizen).build());
        when(eligibilityRepository.save(any(EligibilityDetails.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        EligibilityDetails result = edService.determineEligibility(101L);

        // Assert
        assertEquals("Medicaid", result.getPlanName());
        assertEquals("APPROVED", result.getPlanStatus());
    }

    @Test
    void determineEligibility_ShouldApproveQHP_WhenIncomeHigh() {
        // Arrange
        dcSummary.getIncome().setSalaryIncome(5000.0); // Total 5000 (High Income)

        when(dcApiClient.getDcSummary(101L)).thenReturn(ApiResponse.<DcSummary>builder().data(dcSummary).build());
        when(arApiClient.getCitizen(101L)).thenReturn(ApiResponse.<Citizen>builder().data(citizen).build());
        when(eligibilityRepository.save(any(EligibilityDetails.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        EligibilityDetails result = edService.determineEligibility(101L);

        // Assert
        assertEquals("QHP", result.getPlanName());
        assertEquals("APPROVED", result.getPlanStatus());
    }
}
