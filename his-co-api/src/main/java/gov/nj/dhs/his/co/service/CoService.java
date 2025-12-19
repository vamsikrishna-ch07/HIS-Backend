package gov.nj.dhs.his.co.service;

import gov.nj.dhs.his.co.entity.CoTrigger;
import reactor.core.publisher.Mono;

public interface CoService {
    Mono<CoTrigger> processCorrespondence(Long appId);
    Mono<Void> generateCorrespondence(Long appId, String status);
}
