package gov.nj.dhs.his.reports.controller;

import gov.nj.dhs.his.reports.model.Report;
import gov.nj.dhs.his.reports.service.ReportsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/reports")
public class ReportsController {

    private final ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @GetMapping("/generate/{appId}")
    public Mono<Report> generateReport(@PathVariable Long appId) {
        return reportsService.generateReport(appId);
    }
}