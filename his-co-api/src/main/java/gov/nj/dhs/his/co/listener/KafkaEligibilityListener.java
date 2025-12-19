package gov.nj.dhs.his.co.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nj.dhs.his.co.model.EligibilityEvent;
import gov.nj.dhs.his.co.service.CoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaEligibilityListener {

    private static final Logger logger = LoggerFactory.getLogger(KafkaEligibilityListener.class);

    private final CoService coService;
    private final ObjectMapper objectMapper;

    public KafkaEligibilityListener(CoService coService, ObjectMapper objectMapper) {
        this.coService = coService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "eligibility-approved", groupId = "co-group")
    public void handleApproval(String message) {
        try {
            EligibilityEvent event = objectMapper.readValue(message, EligibilityEvent.class);
            logger.info("Received approval event for app ID: {}", event.getApplicationId());
            coService.generateCorrespondence(event.getApplicationId(), "APPROVED").subscribe();
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing approval event", e);
        }
    }

    @KafkaListener(topics = "eligibility-denied", groupId = "co-group")
    public void handleDenial(String message) {
        try {
            EligibilityEvent event = objectMapper.readValue(message, EligibilityEvent.class);
            logger.info("Received denial event for app ID: {}", event.getApplicationId());
            coService.generateCorrespondence(event.getApplicationId(), "DENIED").subscribe();
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing denial event", e);
        }
    }
}
