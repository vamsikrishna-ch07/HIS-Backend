package gov.nj.dhs.his.reports.service;

import gov.nj.dhs.his.common.ApiResponse;
import gov.nj.dhs.his.reports.feign.ArApiClient;
import gov.nj.dhs.his.reports.feign.EdApiClient;
import gov.nj.dhs.his.reports.model.CitizenApplication;
import gov.nj.dhs.his.reports.model.EligibilityDetails;
import gov.nj.dhs.his.reports.model.Report;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ReportsServiceImpl implements ReportsService {

    private final ArApiClient arApiClient;
    private final EdApiClient edApiClient;

    public ReportsServiceImpl(ArApiClient arApiClient, EdApiClient edApiClient) {
        this.arApiClient = arApiClient;
        this.edApiClient = edApiClient;
    }

    @Override
    public Mono<Report> generateReport(Long appId) {
        // Fetch data from both services concurrently
        Mono<ApiResponse<CitizenApplication>> appResponseMono = arApiClient.getApplicationById(appId);
        Mono<ApiResponse<EligibilityDetails>> eligResponseMono = edApiClient.getEligibilityDetails(appId);

        // Use Mono.zip to combine the results when both have arrived
        return Mono.zip(appResponseMono, eligResponseMono)
                .map(tuple -> {
                    CitizenApplication app = tuple.getT1().getData();
                    EligibilityDetails elig = tuple.getT2().getData();

                    // Build the consolidated report
                    return Report.builder()
                            .appId(app.getAppId())
                            .fullName(app.getFullName())
                            .email(app.getEmail())
                            .ssn(app.getSsn())
                            .planName(elig.getPlanName())
                            .planStatus(elig.getPlanStatus())
                            .benefitAmount(elig.getBenefitAmount())
                            .startDate(elig.getStartDate())
                            .endDate(elig.getEndDate())
                            .denialReason(elig.getDenialReason())
                            .build();
                });
    }
}
