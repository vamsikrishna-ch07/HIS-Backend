package gov.nj.dhs.his.co.controller;

import gov.nj.dhs.his.co.entity.CoTrigger;
import gov.nj.dhs.his.co.service.CoService;
import gov.nj.dhs.his.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/co")
public class CoController {

    private final CoService coService;

    public CoController(CoService coService) {
        this.coService = coService;
    }

    @GetMapping("/generate/{appId}")
    public Mono<ApiResponse<CoTrigger>> generateCorrespondence(@PathVariable Long appId) {
        return coService.processCorrespondence(appId)
                .map(trigger -> ApiResponse.<CoTrigger>builder()
                        .status("SUCCESS")
                        .message("Correspondence processed successfully.")
                        .data(trigger)
                        .build());
    }
}
