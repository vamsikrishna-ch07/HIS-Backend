package gov.nj.dhs.his.co.service;

import gov.nj.dhs.his.co.entity.CoTrigger;
import gov.nj.dhs.his.co.feign.EdApiClient;
import gov.nj.dhs.his.co.model.EligibilityDetails;
import gov.nj.dhs.his.co.repository.CoTriggerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
                    if (eligDetails == null) {
                        CoTrigger failedTrigger = CoTrigger.builder()
                                .appId(appId)
                                .triggerStatus("FAILED")
                                .failureReason("Eligibility details not found.")
                                .build();
                        return coTriggerRepository.save(failedTrigger);
                    }

                    byte[] pdf = generateNoticeContent(eligDetails).getBytes();
                    CoTrigger successTrigger = CoTrigger.builder()
                            .appId(appId)
                            .noticePdf(pdf)
                            .triggerStatus("COMPLETED")
                            .build();
                    return coTriggerRepository.save(successTrigger);
                })
                .doOnSuccess(trigger -> logger.info("Correspondence processed successfully for appId={}", trigger.getAppId()))
                .doOnError(error -> logger.error("Error processing correspondence for appId={}", appId, error))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> generateCorrespondence(Long appId, String status) {
        logger.info("Processing Kafka {} event for appId={}", status, appId);
        CoTrigger trigger = CoTrigger.builder()
                .appId(appId)
                .triggerStatus("COMPLETED_FROM_KAFKA")
                .build();

        return coTriggerRepository.save(trigger)
                .doOnSuccess(savedTrigger -> logger.info("Successfully saved Kafka-triggered correspondence for appId={}", savedTrigger.getAppId()))
                .doOnError(error -> logger.error("Failed to save Kafka-triggered correspondence for appId={}", appId, error))
                .then();
    }

    private String generateNoticeContent(EligibilityDetails eligDetails) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- NOTICE OF ELIGIBILITY DETERMINATION ---\n\n");
        sb.append("Application ID: ").append(eligDetails.getAppId()).append("\n");
        sb.append("Status: ").append(eligDetails.getPlanStatus()).append("\n\n");

        if ("APPROVED".equalsIgnoreCase(eligDetails.getPlanStatus())) {
            sb.append("Plan Name: ").append(eligDetails.getPlanName()).append("\n");
            sb.append("Benefit Amount: ").append(eligDetails.getBenefitAmount()).append("\n");
            sb.append("Start Date: ").append(eligDetails.getStartDate()).append("\n");
            sb.append("End Date: ").append(eligDetails.getEndDate()).append("\n");
        } else {
            sb.append("Reason: ").append(eligDetails.getDenialReason()).append("\n");
        }

        sb.append("\n--- END OF NOTICE ---");
        return sb.toString();
    }
}
