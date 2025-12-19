package gov.nj.dhs.his.bi.service;

import gov.nj.dhs.his.bi.entity.PendingBenefit;
import gov.nj.dhs.his.bi.model.EligibilityEvent;
import gov.nj.dhs.his.bi.repository.PendingBenefitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BenefitServiceImpl implements BenefitService {

    private static final Logger logger = LoggerFactory.getLogger(BenefitServiceImpl.class);

    private final PendingBenefitRepository pendingBenefitRepository;

    public BenefitServiceImpl(PendingBenefitRepository pendingBenefitRepository) {
        this.pendingBenefitRepository = pendingBenefitRepository;
    }

    @Override
    public void savePendingBenefit(EligibilityEvent event) {
        logger.info("Saving pending benefit for application ID: {}", event.getApplicationId());

        PendingBenefit pendingBenefit = new PendingBenefit();
        pendingBenefit.setApplicationId(event.getApplicationId());
        pendingBenefit.setCitizenId(event.getCitizenId());
        pendingBenefit.setPlanName(event.getPlanName());
        pendingBenefit.setBenefitAmount(event.getBenefitAmount());

        pendingBenefitRepository.save(pendingBenefit);
    }
}
