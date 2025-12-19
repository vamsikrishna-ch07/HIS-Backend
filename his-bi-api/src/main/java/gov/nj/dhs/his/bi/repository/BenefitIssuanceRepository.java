package gov.nj.dhs.his.bi.repository;

import gov.nj.dhs.his.bi.entity.BenefitIssuance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenefitIssuanceRepository extends JpaRepository<BenefitIssuance, Long> {
}