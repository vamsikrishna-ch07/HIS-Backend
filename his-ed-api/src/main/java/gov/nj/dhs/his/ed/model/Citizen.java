package gov.nj.dhs.his.ed.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Citizen {
    private Long appId;
    private String fullName;
    private String email;
    private Long phNo;
    private String gender;
    private Long ssn;
    private LocalDate dob;
    private String stateName;
}
