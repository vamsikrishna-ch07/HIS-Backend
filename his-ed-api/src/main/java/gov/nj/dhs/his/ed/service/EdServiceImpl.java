package gov.nj.dhs.his.ed.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nj.dhs.his.common.ApiResponse;
import gov.nj.dhs.his.ed.entity.EligibilityDetails;
import gov.nj.dhs.his.ed.feign.ArApiClient;
import gov.nj.dhs.his.ed.feign.DcApiClient;
import gov.nj.dhs.his.ed.model.Citizen;
import gov.nj.dhs.his.ed.model.DcSummary;
import gov.nj.dhs.his.ed.model.EligibilityEvent;
import gov.nj.dhs.his.ed.repository.EligibilityDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class EdServiceImpl implements EdService {

    private static final Logger logger = LoggerFactory.getLogger(EdServiceImpl.class);
    private static final String TOPIC_ELIGIBILITY_APPROVED = "eligibility-approved";
    private static final String TOPIC_ELIGIBILITY_DENIED = "eligibility-denied";

    private final DcApiClient dcApiClient;
    private final ArApiClient arApiClient;
    private final EligibilityDetailsRepository eligibilityRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public EdServiceImpl(DcApiClient dcApiClient, ArApiClient arApiClient, EligibilityDetailsRepository eligibilityRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.dcApiClient = dcApiClient;
        this.arApiClient = arApiClient;
        this.eligibilityRepository = eligibilityRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public EligibilityDetails determineEligibility(Long appId) {
        // 1. Call DC API to get summary
        ApiResponse<DcSummary> dcApiResponse = dcApiClient.getDcSummary(appId);
        DcSummary summary = dcApiResponse.getData();

        if (summary == null) {
            return buildErrorResponse(appId, "Data not found for applicant.");
        }

        // 2. Call AR API to get citizen data
        ApiResponse<Citizen> arApiResponse = arApiClient.getCitizen(appId);
        Citizen citizen = arApiResponse.getData();

        if (citizen == null) {
            return buildErrorResponse(appId, "Citizen not found for applicant.");
        }

        // 3. Execute business rules
        EligibilityDetails eligibility = checkEligibility(summary, citizen);

        // 4. Save the determination
        EligibilityDetails savedEligibility = eligibilityRepository.save(eligibility);

        // 5. Send Kafka Event
        // Use SSN from Citizen service as the citizenId
        sendEligibilityEvent(savedEligibility, citizen.getSsn());

        return savedEligibility;
    }

    private EligibilityDetails checkEligibility(DcSummary summary, Citizen citizen) {
        EligibilityDetails eligibility = new EligibilityDetails();
        eligibility.setAppId(summary.getAppId());

        // Calculate Total Income with null checks
        double totalIncome = 0.0;
        if (summary.getIncome() != null) {
            totalIncome = (summary.getIncome().getSalaryIncome() != null ? summary.getIncome().getSalaryIncome() : 0.0)
                        + (summary.getIncome().getRentIncome() != null ? summary.getIncome().getRentIncome() : 0.0)
                        + (summary.getIncome().getPropertyIncome() != null ? summary.getIncome().getPropertyIncome() : 0.0);
        }

        // Calculate Age with null check
        int age = 0;
        if (citizen.getDob() != null) {
            age = Period.between(citizen.getDob(), LocalDate.now()).getYears();
        }

        // Calculate Kids Count with null check
        int kidsCount = (summary.getKids() != null) ? summary.getKids().size() : 0;

        // ============================================================
        // PRIORITY ORDER: Medicare -> CCAP -> SNAP -> Medicaid -> QHP
        // ============================================================

        // 1. Medicare (Age >= 65)
        if (age >= 65) {
            return approvePlan(eligibility, "Medicare", 5000.00);
        }

        // 2. CCAP (Income <= 2500 AND Kids > 0)
        if (totalIncome <= 2500 && kidsCount > 0) {
            return approvePlan(eligibility, "CCAP", 300.00);
        }

        // 3. SNAP (Income <= 2000)
        if (totalIncome <= 2000) {
            return approvePlan(eligibility, "SNAP", 250.00);
        }

        // 4. Medicaid (Income <= 3000)
        if (totalIncome <= 3000) {
            return approvePlan(eligibility, "Medicaid", 350.00);
        }

        // 5. QHP (Fallback - Commercial Plan)
        return approvePlan(eligibility, "QHP", null);
    }

    private EligibilityDetails approvePlan(EligibilityDetails eligibility, String planName, Double benefitAmount) {
        eligibility.setPlanName(planName);
        eligibility.setPlanStatus("APPROVED");
        eligibility.setBenefitAmount(benefitAmount);
        eligibility.setStartDate(LocalDate.now());
        eligibility.setEndDate(LocalDate.now().plusYears(1));
        eligibility.setDenialReason(null); // Clear any previous denial reason
        return eligibility;
    }

    private void sendEligibilityEvent(EligibilityDetails eligibility, Long citizenId) {
        EligibilityEvent event = new EligibilityEvent();
        event.setApplicationId(eligibility.getAppId());
        event.setCitizenId(citizenId);
        event.setPlanName(eligibility.getPlanName());
        event.setBenefitAmount(eligibility.getBenefitAmount());

        try {
            String payload = objectMapper.writeValueAsString(event);
            if ("APPROVED".equals(eligibility.getPlanStatus())) {
                kafkaTemplate.send(TOPIC_ELIGIBILITY_APPROVED, payload);
                logger.info("Sent approved event for app ID: {}", eligibility.getAppId());
            } else if ("DENIED".equals(eligibility.getPlanStatus())) {
                kafkaTemplate.send(TOPIC_ELIGIBILITY_DENIED, payload);
                logger.info("Sent denied event for app ID: {}", eligibility.getAppId());
            }
        } catch (JsonProcessingException e) {
            logger.error("Error serializing eligibility event for app ID: {}", eligibility.getAppId(), e);
        }
    }

    @Override
    public Optional<EligibilityDetails> getEligibility(Long appId) {
        return eligibilityRepository.findByAppId(appId);
    }

    @Override
    public List<EligibilityDetails> getApprovedEligibility() {
        return eligibilityRepository.findByPlanStatus("APPROVED");
    }

    private EligibilityDetails buildErrorResponse(Long appId, String message) {
        EligibilityDetails errorDetails = new EligibilityDetails();
        errorDetails.setAppId(appId);
        errorDetails.setPlanStatus("ERROR");
        errorDetails.setDenialReason(message);
        return eligibilityRepository.save(errorDetails);
    }
}
