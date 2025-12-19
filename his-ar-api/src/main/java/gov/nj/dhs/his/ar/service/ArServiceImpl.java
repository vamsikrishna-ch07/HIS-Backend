package gov.nj.dhs.his.ar.service;

import gov.nj.dhs.his.ar.entity.CitizenApplication;
import gov.nj.dhs.his.ar.repository.CitizenApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ArServiceImpl implements ArService {

    private final CitizenApplicationRepository applicationRepository;

    public ArServiceImpl(CitizenApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Override
    public CitizenApplication createApplication(CitizenApplication application) {
        // In a real system, we would have more logic here,
        // such as checking for duplicate SSNs, validating state, etc.
        // For now, we will just save the application.
        return applicationRepository.save(application);
    }

    @Override
    public Optional<CitizenApplication> getApplicationById(Long appId) {
        return applicationRepository.findById(appId);
    }
}