package gov.nj.dhs.his.bi.service;

import gov.nj.dhs.his.bi.model.EligibilityEvent;

public interface BenefitService {
    void savePendingBenefit(EligibilityEvent event);
}
