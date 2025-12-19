package gov.nj.dhs.his.ed.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nj.dhs.his.common.ApiResponse;
import gov.nj.dhs.his.ed.entity.EligibilityDetails;
import gov.nj.dhs.his.ed.feign.DcApiClient;
import gov.nj.dhs.his.ed.model.DcSummary;
import gov.nj.dhs.his.ed.model.EligibilityEvent;
import gov.nj.dhs.his.ed.repository.EligibilityDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EdServiceImpl implements EdService {

    private static final Logger logger = LoggerFactory.getLogger(EdServiceImpl.class);
    private static final String TOPIC_ELIGIBILITY_APPROVED = "eligibility-approved";
    private static final String TOPIC_ELIGIBILITY_DENIED = "eligibility-denied";

    private final DcApiClient dcApiClient;
    private final EligibilityDetailsRepository eligibilityRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    public EdServiceImpl(DcApiClient dcApiClient, EligibilityDetailsRepository eligibilityRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.dcApiClient = dcApiClient;
        this.eligibilityRepository = eligibilityRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public EligibilityDetails determineEligibility(Long appId) {
        // 1. Call DC API to get summary
        ApiResponse<DcSummary> apiResponse = dcApiClient.getDcSummary(appId);
        DcSummary summary = apiResponse.getData();

        if (summary == null) {
            // Handle case where no data is found
            return buildErrorResponse(appId, "Data not found for applicant.");
        }

        // 2. Execute business rules (simplified)
        EligibilityDetails eligibility = new EligibilityDetails();
        eligibility.setAppId(appId);

        // Rule 1: Check income
        if (summary.getIncome().getSalaryIncome() > 50000) {
            eligibility.setPlanStatus("DENIED");
            eligibility.setDenialReason("Income exceeds the maximum threshold.");
        }
        // Rule 2: Check education (example)
        else if (summary.getEducation().getHighestDegree().equalsIgnoreCase("B.Tech")) {
            eligibility.setPlanStatus("APPROVED");
            eligibility.setPlanName("NJ-CARE-PRIME");
            eligibility.setBenefitAmount(500.00);
            eligibility.setStartDate(LocalDate.now());
            eligibility.setEndDate(LocalDate.now().plusYears(1));
        }
        // Rule 3: Check kids (example)
        else if (summary.getKids() != null && !summary.getKids().isEmpty()) {
            eligibility.setPlanStatus("APPROVED");
            eligibility.setPlanName("NJ-CARE-KIDS");
            eligibility.setBenefitAmount(300.00);
            eligibility.setStartDate(LocalDate.now());
            eligibility.setEndDate(LocalDate.now().plusYears(1));
        } else {
            eligibility.setPlanStatus("DENIED");
            eligibility.setDenialReason("No applicable plan found based on provided data.");
        }

        // 3. Save the determination
        EligibilityDetails savedEligibility = eligibilityRepository.save(eligibility);

        // 4. Send Kafka Event
        sendEligibilityEvent(savedEligibility, summary.getCitizenId());


        return savedEligibility;
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