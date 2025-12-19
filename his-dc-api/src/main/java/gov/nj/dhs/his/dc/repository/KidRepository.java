package gov.nj.dhs.his.dc.repository;

import gov.nj.dhs.his.dc.entity.DcCase;
import gov.nj.dhs.his.dc.entity.Kid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KidRepository extends JpaRepository<Kid, Long> {
    List<Kid> findByDcCase(DcCase dcCase);
}