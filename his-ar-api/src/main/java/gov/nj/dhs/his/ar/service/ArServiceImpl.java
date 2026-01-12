package gov.nj.dhs.his.ar.service;

import gov.nj.dhs.his.ar.entity.CitizenApplication;
import gov.nj.dhs.his.ar.repository.CitizenApplicationRepository;
import gov.nj.dhs.his.common.exception.HisException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ArServiceImpl implements ArService {

    private static final String ALLOWED_STATE = "New Jersey";

    private final CitizenApplicationRepository applicationRepository;

    public ArServiceImpl(CitizenApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Override
    public CitizenApplication createApplication(CitizenApplication application) {
        // 1. Check for Duplicate SSN
        if (applicationRepository.existsBySsn(application.getSsn())) {
            throw new HisException("Application already exists with this SSN: " + application.getSsn());
        }

        // 2. Validate State (Must be New Jersey)
        if (application.getStateName() == null || !ALLOWED_STATE.equalsIgnoreCase(application.getStateName())) {
            throw new HisException("Application is only allowed for residents of " + ALLOWED_STATE);
        }

        // 3. Save Application
        return applicationRepository.save(application);
    }

    @Override
    public Optional<CitizenApplication> getApplicationById(Long appId) {
        return applicationRepository.findById(appId);
    }
}
