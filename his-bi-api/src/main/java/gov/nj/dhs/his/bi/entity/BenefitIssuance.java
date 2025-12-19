package gov.nj.dhs.his.bi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "BENEFIT_ISSUANCE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BenefitIssuance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ISSUANCE_ID")
    private Long issuanceId;

    @Column(name = "APP_ID", nullable = false)
    private Long appId;

    @Column(name = "CITIZEN_ID", nullable = false)
    private Long citizenId;

    @Column(name = "PLAN_NAME")
    private String planName;

    @Column(name = "BENEFIT_AMOUNT", nullable = false)
    private Double benefitAmount;

    @Column(name = "ISSUANCE_DATE", nullable = false)
    private LocalDate issuanceDate;

    @Column(name = "STATUS", nullable = false)
    private String status; // e.g., "ISSUED", "FAILED"

    @Column(name = "CREATED_DATE", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;
}
