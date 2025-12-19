package gov.nj.dhs.his.co.repository;

import gov.nj.dhs.his.co.entity.CoTrigger;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoTriggerRepository extends ReactiveCrudRepository<CoTrigger, Long> {
}
