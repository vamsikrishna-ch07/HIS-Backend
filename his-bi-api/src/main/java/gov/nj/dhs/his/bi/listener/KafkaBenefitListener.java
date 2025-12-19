package gov.nj.dhs.his.bi.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nj.dhs.his.bi.model.EligibilityEvent;
import gov.nj.dhs.his.bi.service.BenefitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaBenefitListener {

    private static final Logger logger = LoggerFactory.getLogger(KafkaBenefitListener.class);

    private final BenefitService benefitService;
    private final ObjectMapper objectMapper;

    public KafkaBenefitListener(BenefitService benefitService, ObjectMapper objectMapper) {
        this.benefitService = benefitService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "eligibility-approved", groupId = "bi-group")
    public void handleApproval(String message) {
        try {
            EligibilityEvent event = objectMapper.readValue(message, EligibilityEvent.class);
            logger.info("Received approval event for benefit issuance. App ID: {}", event.getApplicationId());
            benefitService.savePendingBenefit(event);
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing approval event for benefit issuance", e);
        }
    }
}
