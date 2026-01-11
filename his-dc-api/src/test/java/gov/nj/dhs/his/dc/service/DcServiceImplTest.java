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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DcServiceImplTest {

    @Mock
    private DcCaseRepository caseRepository;

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private KidRepository kidRepository;

    @InjectMocks
    private DcServiceImpl dcService;

    private DcSummary dcSummary;
    private DcCase dcCase;

    @BeforeEach
    void setUp() {
        dcCase = DcCase.builder()
                .caseId(1L)
                .appId(101L)
                .build();

        Income income = Income.builder()
                .salaryIncome(5000.0)
                .rentIncome(1000.0)
                .propertyIncome(0.0)
                .build();

        Education education = new Education();
        education.setHighestDegree("Bachelors");
        education.setGraduationYear(2020);

        List<Kid> kids = new ArrayList<>();
        Kid kid1 = new Kid();
        kid1.setKidName("Kid1");
        kid1.setKidAge(5);
        kids.add(kid1);

        dcSummary = new DcSummary();
        dcSummary.setAppId(101L);
        dcSummary.setIncome(income);
        dcSummary.setEducation(education);
        dcSummary.setKids(kids);
    }

    @Test
    void saveSummary_ShouldSaveAllDetails_WhenCaseExists() {
        // Arrange
        when(caseRepository.findByAppId(101L)).thenReturn(Optional.of(dcCase));
        when(incomeRepository.save(any(Income.class))).thenReturn(dcSummary.getIncome());
        when(educationRepository.save(any(Education.class))).thenReturn(dcSummary.getEducation());
        when(kidRepository.saveAll(anyList())).thenReturn(dcSummary.getKids());

        // Act
        boolean result = dcService.saveSummary(dcSummary);

        // Assert
        assertTrue(result);
        verify(caseRepository, times(1)).findByAppId(101L);
        verify(incomeRepository, times(1)).save(any(Income.class));
        verify(educationRepository, times(1)).save(any(Education.class));
        verify(kidRepository, times(1)).saveAll(anyList());
    }

    @Test
    void saveSummary_ShouldCreateNewCase_WhenCaseDoesNotExist() {
        // Arrange
        when(caseRepository.findByAppId(101L)).thenReturn(Optional.empty());
        when(caseRepository.save(any(DcCase.class))).thenReturn(dcCase);
        when(incomeRepository.save(any(Income.class))).thenReturn(dcSummary.getIncome());
        when(educationRepository.save(any(Education.class))).thenReturn(dcSummary.getEducation());
        when(kidRepository.saveAll(anyList())).thenReturn(dcSummary.getKids());

        // Act
        boolean result = dcService.saveSummary(dcSummary);

        // Assert
        assertTrue(result);
        verify(caseRepository, times(1)).save(any(DcCase.class)); // Verify new case creation
        verify(incomeRepository, times(1)).save(any(Income.class));
    }

    @Test
    void getSummary_ShouldReturnSummary_WhenCaseExists() {
        // Arrange
        when(caseRepository.findByAppId(101L)).thenReturn(Optional.of(dcCase));
        when(incomeRepository.findByDcCase(dcCase)).thenReturn(Optional.of(dcSummary.getIncome()));
        when(educationRepository.findByDcCase(dcCase)).thenReturn(Optional.of(dcSummary.getEducation()));
        when(kidRepository.findByDcCase(dcCase)).thenReturn(dcSummary.getKids());

        // Act
        DcSummary result = dcService.getSummary(101L);

        // Assert
        assertNotNull(result);
        assertEquals(101L, result.getAppId());
        assertEquals(5000.0, result.getIncome().getSalaryIncome());
        assertEquals("Bachelors", result.getEducation().getHighestDegree());
        assertEquals(1, result.getKids().size());
    }

    @Test
    void getSummary_ShouldReturnNull_WhenCaseDoesNotExist() {
        // Arrange
        when(caseRepository.findByAppId(999L)).thenReturn(Optional.empty());

        // Act
        DcSummary result = dcService.getSummary(999L);

        // Assert
        assertNull(result);
    }
}
