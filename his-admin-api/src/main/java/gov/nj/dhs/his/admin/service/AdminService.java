package gov.nj.dhs.his.admin.service;

import gov.nj.dhs.his.admin.entity.InsurancePlan;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    InsurancePlan savePlan(InsurancePlan plan);
    List<InsurancePlan> getAllPlans();
    Optional<InsurancePlan> getPlanById(Long planId);
    InsurancePlan updatePlan(Long planId, InsurancePlan plan);
    boolean deactivatePlan(Long planId);
}