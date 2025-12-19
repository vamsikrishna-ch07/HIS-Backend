package gov.nj.dhs.his.ed.model;

import lombok.Data;

import java.util.List;

@Data
public class DcSummary {
    private Long appId;
    private Long citizenId;
    private Income income;
    private Education education;
    private List<Kid> kids;
}
