package gov.nj.dhs.his.reports.service;

import gov.nj.dhs.his.reports.model.Report;
import reactor.core.publisher.Mono;

public interface ReportsService {
    Mono<Report> generateReport(Long appId);
}