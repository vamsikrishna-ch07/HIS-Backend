package gov.nj.dhs.his.dc.model;

import gov.nj.dhs.his.dc.entity.Education;
import gov.nj.dhs.his.dc.entity.Income;
import gov.nj.dhs.his.dc.entity.Kid;
import lombok.Data;

import java.util.List;

@Data
public class DcSummary {
    private Long appId;
    private Income income;
    private Education education;
    private List<Kid> kids;
}