package gov.nj.dhs.his.dc.service;

import gov.nj.dhs.his.dc.model.DcSummary;

public interface DcService {
    boolean saveSummary(DcSummary summary);
    DcSummary getSummary(Long appId);
}