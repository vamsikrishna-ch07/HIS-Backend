package gov.nj.dhs.his.admin.service;

import gov.nj.dhs.his.admin.entity.InsurancePlan;
import gov.nj.dhs.his.admin.entity.PlanStatus;
import gov.nj.dhs.his.admin.repository.InsurancePlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private final InsurancePlanRepository insurancePlanRepository;

    public AdminServiceImpl(InsurancePlanRepository insurancePlanRepository) {
        this.insurancePlanRepository = insurancePlanRepository;
    }

    @Override
    public InsurancePlan savePlan(InsurancePlan plan) {
        return insurancePlanRepository.save(plan);
    }

    @Override
    public List<InsurancePlan> getAllPlans() {
        return insurancePlanRepository.findAll();
    }

    @Override
    public Optional<InsurancePlan> getPlanById(Long planId) {
        return insurancePlanRepository.findById(planId);
    }

    @Override
    public InsurancePlan updatePlan(Long planId, InsurancePlan planDetails) {
        return insurancePlanRepository.findById(planId).map(existingPlan -> {
            existingPlan.setPlanName(planDetails.getPlanName());
            existingPlan.setStartDate(planDetails.getStartDate());
            existingPlan.setEndDate(planDetails.getEndDate());
            existingPlan.setPlanStatus(planDetails.getPlanStatus());
            return insurancePlanRepository.save(existingPlan);
        }).orElse(null); // Or throw a custom exception
    }

    @Override
    public boolean deactivatePlan(Long planId) {
        return insurancePlanRepository.findById(planId).map(plan -> {
            plan.setPlanStatus(PlanStatus.INACTIVE);
            insurancePlanRepository.save(plan);
            return true;
        }).orElse(false);
    }
}