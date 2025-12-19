package gov.nj.dhs.his.co.service;

import gov.nj.dhs.his.co.entity.CoTrigger;
import gov.nj.dhs.his.co.feign.EdApiClient;
import gov.nj.dhs.his.co.model.EligibilityDetails;
import gov.nj.dhs.his.co.repository.CoTriggerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CoServiceImpl implements CoService {

    private static final Logger logger = LoggerFactory.getLogger(CoServiceImpl.class);

    private final EdApiClient edApiClient;
    private final CoTriggerRepository coTriggerRepository;

    public CoServiceImpl(EdApiClient edApiClient, CoTriggerRepository coTriggerRepository) {
        this.edApiClient = edApiClient;
        this.coTriggerRepository = coTriggerRepository;
    }

    @Override
    public Mono<CoTrigger> processCorrespondence(Long appId) {
        return edApiClient.getEligibilityDetails(appId)
                .flatMap(response -> {
                    EligibilityDetails eligDetails = response.getData();
                    CoTrigger trigger = new CoTrigger();
                    trigger.setAppId(appId);

                    if (eligDetails == null) {
                        trigger.setTriggerStatus("FAILED");
                        trigger.setFailureReason("Eligibility details not found.");
                        return coTriggerRepository.save(trigger);
                    }

                    // In a real app, this would be a more complex PDF generation
                    String noticeContent = generateNoticeContent(eligDetails);
                    byte[] pdf = noticeContent.getBytes();

                    trigger.setNoticePdf(pdf);
                    trigger.setTriggerStatus("COMPLETED");

                    return coTriggerRepository.save(trigger);
                })
                .doOnSuccess(trigger -> logger.info("Successfully processed correspondence for app ID: {}", trigger.getAppId()))
                .doOnError(error -> logger.error("Error processing correspondence for app ID: {}", appId, error));
    }

    @Override
    public Mono<Void> generateCorrespondence(Long appId, String status) {
        logger.info("Generating {} correspondence via Kafka event for application ID: {}", status, appId);
        CoTrigger trigger = new CoTrigger();
        trigger.setAppId(appId);
        trigger.setTriggerStatus("COMPLETED_FROM_KAFKA");
        return coTriggerRepository.save(trigger).then();
    }

    private String generateNoticeContent(EligibilityDetails eligDetails) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- NOTICE OF ELIGIBILITY DETERMINATION ---\n\n");
        sb.append("Application ID: ").append(eligDetails.getAppId()).append("\n");
        sb.append("Determination Status: ").append(eligDetails.getPlanStatus()).append("\n\n");

        if ("APPROVED".equalsIgnoreCase(eligDetails.getPlanStatus())) {
            sb.append("Plan Name: ").append(eligDetails.getPlanName()).append("\n");
            sb.append("Benefit Amount: $").append(String.format("%.2f", eligDetails.getBenefitAmount())).append("\n");
            sb.append("Plan Start Date: ").append(eligDetails.getStartDate()).append("\n");
            sb.append("Plan End Date: ").append(eligDetails.getEndDate()).append("\n");
        } else {
            sb.append("Reason for Denial: ").append(eligDetails.getDenialReason()).append("\n");
        }

        sb.append("\n--- End of Notice ---");
        return sb.toString();
    }
}
