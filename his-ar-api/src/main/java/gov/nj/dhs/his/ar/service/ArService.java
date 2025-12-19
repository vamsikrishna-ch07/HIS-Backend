package gov.nj.dhs.his.ar.service;

import gov.nj.dhs.his.ar.entity.CitizenApplication;

import java.util.Optional;

public interface ArService {
    CitizenApplication createApplication(CitizenApplication application);
    Optional<CitizenApplication> getApplicationById(Long appId);
}