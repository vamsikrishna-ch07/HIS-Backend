package gov.nj.dhs.his.ar.repository;

import gov.nj.dhs.his.ar.entity.CitizenApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitizenApplicationRepository extends JpaRepository<CitizenApplication, Long> {
}