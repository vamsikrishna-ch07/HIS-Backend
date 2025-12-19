package gov.nj.dhs.his.bi.repository;

import gov.nj.dhs.his.bi.entity.PendingBenefit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PendingBenefitRepository extends JpaRepository<PendingBenefit, Long> {
    List<PendingBenefit> findByStatus(String status);
}
