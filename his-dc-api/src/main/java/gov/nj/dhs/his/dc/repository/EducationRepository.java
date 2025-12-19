package gov.nj.dhs.his.dc.repository;

import gov.nj.dhs.his.dc.entity.DcCase;
import gov.nj.dhs.his.dc.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    Optional<Education> findByDcCase(DcCase dcCase);
}