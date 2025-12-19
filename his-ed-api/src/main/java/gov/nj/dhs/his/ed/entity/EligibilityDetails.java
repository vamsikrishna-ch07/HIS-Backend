package gov.nj.dhs.his.ed.entity;

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
@Table(name = "ELIGIBILITY_DETAILS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibilityDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ELIG_ID")
    private Long eligId;

    @Column(name = "APP_ID", nullable = false, unique = true)
    private Long appId;

    @Column(name = "PLAN_NAME")
    private String planName;

    @Column(name = "PLAN_STATUS")
    private String planStatus; // e.g., APPROVED, DENIED

    @Column(name = "BENEFIT_AMOUNT")
    private Double benefitAmount;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    @Column(name = "DENIAL_REASON")
    private String denialReason;

    @Column(name = "CREATED_DATE", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(name = "UPDATED_DATE")
    @UpdateTimestamp
    private LocalDateTime updatedDate;
}