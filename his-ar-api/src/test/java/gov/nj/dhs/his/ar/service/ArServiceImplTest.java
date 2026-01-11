package gov.nj.dhs.his.ar.service;

import gov.nj.dhs.his.ar.entity.CitizenApplication;
import gov.nj.dhs.his.ar.repository.CitizenApplicationRepository;
import gov.nj.dhs.his.common.exception.HisException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArServiceImplTest {

    @Mock
    private CitizenApplicationRepository applicationRepository;

    @InjectMocks
    private ArServiceImpl arService;

    private CitizenApplication validApplication;

    @BeforeEach
    void setUp() {
        validApplication = CitizenApplication.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .phNo(1234567890L)
                .gender("Male")
                .ssn(123456789L)
                .stateName("New Jersey")
                .build();
    }

    @Test
    void createApplication_ShouldSaveApplication_WhenValid() {
        // Arrange
        when(applicationRepository.existsBySsn(validApplication.getSsn())).thenReturn(false);
        when(applicationRepository.save(any(CitizenApplication.class))).thenReturn(validApplication);

        // Act
        CitizenApplication savedApp = arService.createApplication(validApplication);

        // Assert
        assertNotNull(savedApp);
        assertEquals("New Jersey", savedApp.getStateName());
        verify(applicationRepository, times(1)).save(any(CitizenApplication.class));
    }

    @Test
    void createApplication_ShouldThrowException_WhenSsnExists() {
        // Arrange
        when(applicationRepository.existsBySsn(validApplication.getSsn())).thenReturn(true);

        // Act & Assert
        HisException exception = assertThrows(HisException.class, () -> {
            arService.createApplication(validApplication);
        });

        assertEquals("Application already exists with this SSN: " + validApplication.getSsn(), exception.getMessage());
        verify(applicationRepository, never()).save(any(CitizenApplication.class));
    }

    @Test
    void createApplication_ShouldThrowException_WhenStateIsNotNJ() {
        // Arrange
        validApplication.setStateName("New York");

        // Act & Assert
        HisException exception = assertThrows(HisException.class, () -> {
            arService.createApplication(validApplication);
        });

        assertTrue(exception.getMessage().contains("Application is only allowed for residents of New Jersey"));
        verify(applicationRepository, never()).save(any(CitizenApplication.class));
    }
}
