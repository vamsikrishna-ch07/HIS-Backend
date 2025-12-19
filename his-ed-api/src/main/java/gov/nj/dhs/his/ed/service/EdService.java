package gov.nj.dhs.his.ed.service;

import gov.nj.dhs.his.ed.entity.EligibilityDetails;

import java.util.List;
import java.util.Optional;

public interface EdService {
    EligibilityDetails determineEligibility(Long appId);
    Optional<EligibilityDetails> getEligibility(Long appId);
    List<EligibilityDetails> getApprovedEligibility();
}