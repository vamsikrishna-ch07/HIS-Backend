package gov.nj.dhs.his.reports.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CitizenApplication {
    private Long appId;
    private String fullName;
    private String email;
    private Long phNo;
    private String gender;
    private Long ssn;
    private LocalDate dob;
    private String stateName;
}