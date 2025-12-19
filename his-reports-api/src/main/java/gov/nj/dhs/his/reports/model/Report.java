package gov.nj.dhs.his.reports.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Report {
    private Long appId;
    private String fullName;
    private String email;
    private Long ssn;
    private String planName;
    private String planStatus;
    private Double benefitAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String denialReason;
}