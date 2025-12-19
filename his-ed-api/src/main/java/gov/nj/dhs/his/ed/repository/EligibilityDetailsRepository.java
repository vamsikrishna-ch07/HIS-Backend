package gov.nj.dhs.his.ed.repository;

import gov.nj.dhs.his.ed.entity.EligibilityDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EligibilityDetailsRepository extends JpaRepository<EligibilityDetails, Long> {
    Optional<EligibilityDetails> findByAppId(Long appId);
    List<EligibilityDetails> findByPlanStatus(String planStatus);
}