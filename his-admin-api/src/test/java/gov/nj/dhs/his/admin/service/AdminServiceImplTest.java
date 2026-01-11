package gov.nj.dhs.his.admin.service;

import gov.nj.dhs.his.admin.entity.InsurancePlan;
import gov.nj.dhs.his.admin.entity.PlanStatus;
import gov.nj.dhs.his.admin.repository.InsurancePlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private InsurancePlanRepository insurancePlanRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private InsurancePlan plan;

    @BeforeEach
    void setUp() {
        plan = InsurancePlan.builder()
                .planId(1L)
                .planName("SNAP")
                .planStatus(PlanStatus.ACTIVE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .build();
    }

    @Test
    void savePlan_ShouldReturnSavedPlan() {
        // Arrange
        when(insurancePlanRepository.save(any(InsurancePlan.class))).thenReturn(plan);

        // Act
        InsurancePlan savedPlan = adminService.savePlan(plan);

        // Assert
        assertNotNull(savedPlan);
        assertEquals("SNAP", savedPlan.getPlanName());
        verify(insurancePlanRepository, times(1)).save(plan);
    }

    @Test
    void updatePlan_ShouldReturnUpdatedPlan_WhenPlanExists() {
        // Arrange
        InsurancePlan updatedDetails = InsurancePlan.builder().planName("New SNAP").planStatus(PlanStatus.ACTIVE).startDate(LocalDate.now()).endDate(LocalDate.now().plusYears(2)).build();
        when(insurancePlanRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(insurancePlanRepository.save(any(InsurancePlan.class))).thenReturn(updatedDetails);

        // Act
        InsurancePlan result = adminService.updatePlan(1L, updatedDetails);

        // Assert
        assertNotNull(result);
        assertEquals("New SNAP", result.getPlanName());
        assertEquals(LocalDate.now().plusYears(2), result.getEndDate());
    }

    @Test
    void updatePlan_ShouldReturnNull_WhenPlanDoesNotExist() {
        // Arrange
        when(insurancePlanRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        InsurancePlan result = adminService.updatePlan(99L, new InsurancePlan());

        // Assert
        assertNull(result);
    }

    @Test
    void deactivatePlan_ShouldSetStatusToInactive_WhenPlanExists() {
        // Arrange
        when(insurancePlanRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(insurancePlanRepository.save(any(InsurancePlan.class))).thenReturn(plan);

        // Act
        boolean result = adminService.deactivatePlan(1L);

        // Assert
        assertTrue(result);
        assertEquals(PlanStatus.INACTIVE, plan.getPlanStatus());
        verify(insurancePlanRepository, times(1)).save(plan);
    }

    @Test
    void deactivatePlan_ShouldReturnFalse_WhenPlanDoesNotExist() {
        // Arrange
        when(insurancePlanRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        boolean result = adminService.deactivatePlan(99L);

        // Assert
        assertFalse(result);
        verify(insurancePlanRepository, never()).save(any(InsurancePlan.class));
    }
}
