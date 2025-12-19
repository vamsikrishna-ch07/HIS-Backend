package gov.nj.dhs.his.reports.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EligibilityDetails {
    private Long eligId;
    private Long appId;
    private String planName;
    private String planStatus;
    private Double benefitAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String denialReason;
}