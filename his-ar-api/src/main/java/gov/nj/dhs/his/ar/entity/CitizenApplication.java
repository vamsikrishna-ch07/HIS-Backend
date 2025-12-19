package gov.nj.dhs.his.ar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CITIZEN_APPS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitizenApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APP_ID")
    private Long appId;

    @Column(name = "FULL_NAME", nullable = false)
    private String fullName;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "PH_NO", nullable = false)
    private Long phNo;

    @Column(name = "GENDER", nullable = false)
    private String gender;

    @Column(name = "SSN", nullable = false, unique = true)
    private Long ssn;

    @Column(name = "DOB", nullable = false)
    private LocalDate dob;

    @Column(name = "STATE_NAME")
    private String stateName;

    @Column(name = "CREATED_DATE", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(name = "UPDATED_DATE")
    @UpdateTimestamp
    private LocalDateTime updatedDate;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_BY")
    private String updatedBy;
}