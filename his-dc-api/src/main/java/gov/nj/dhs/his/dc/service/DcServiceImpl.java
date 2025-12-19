package gov.nj.dhs.his.dc.service;

import gov.nj.dhs.his.dc.entity.DcCase;
import gov.nj.dhs.his.dc.entity.Education;
import gov.nj.dhs.his.dc.entity.Income;
import gov.nj.dhs.his.dc.entity.Kid;
import gov.nj.dhs.his.dc.model.DcSummary;
import gov.nj.dhs.his.dc.repository.DcCaseRepository;
import gov.nj.dhs.his.dc.repository.EducationRepository;
import gov.nj.dhs.his.dc.repository.IncomeRepository;
import gov.nj.dhs.his.dc.repository.KidRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DcServiceImpl implements DcService {

    private final DcCaseRepository caseRepository;
    private final IncomeRepository incomeRepository;
    private final EducationRepository educationRepository;
    private final KidRepository kidRepository;

    public DcServiceImpl(DcCaseRepository caseRepository, IncomeRepository incomeRepository,
                         EducationRepository educationRepository, KidRepository kidRepository) {
        this.caseRepository = caseRepository;
        this.incomeRepository = incomeRepository;
        this.educationRepository = educationRepository;
        this.kidRepository = kidRepository;
    }

    @Override
    @Transactional
    public boolean saveSummary(DcSummary summary) {
        // Find or create the case
        DcCase dcCase = caseRepository.findByAppId(summary.getAppId())
                .orElseGet(() -> {
                    DcCase newCase = new DcCase();
                    newCase.setAppId(summary.getAppId());
                    return caseRepository.save(newCase);
                });

        // Save Income
        summary.getIncome().setDcCase(dcCase);
        incomeRepository.save(summary.getIncome());

        // Save Education
        summary.getEducation().setDcCase(dcCase);
        educationRepository.save(summary.getEducation());

        // Save Kids
        List<Kid> kids = summary.getKids();
        kids.forEach(kid -> kid.setDcCase(dcCase));
        kidRepository.saveAll(kids);

        return true;
    }

    @Override
    public DcSummary getSummary(Long appId) {
        Optional<DcCase> caseOpt = caseRepository.findByAppId(appId);
        if (caseOpt.isPresent()) {
            DcCase dcCase = caseOpt.get();
            Income income = incomeRepository.findByDcCase(dcCase).orElse(null);
            Education education = educationRepository.findByDcCase(dcCase).orElse(null);
            List<Kid> kids = kidRepository.findByDcCase(dcCase);

            DcSummary summary = new DcSummary();
            summary.setAppId(appId);
            summary.setIncome(income);
            summary.setEducation(education);
            summary.setKids(kids);
            return summary;
        }
        return null; // Or throw a custom not found exception
    }
}