package gov.nj.dhs.his.admin.repository;

import gov.nj.dhs.his.admin.entity.InsurancePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsurancePlanRepository extends JpaRepository<InsurancePlan, Long> {
}