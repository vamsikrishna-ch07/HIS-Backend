package gov.nj.dhs.his.dc.repository;

import gov.nj.dhs.his.dc.entity.DcCase;
import gov.nj.dhs.his.dc.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    Optional<Income> findByDcCase(DcCase dcCase);
}