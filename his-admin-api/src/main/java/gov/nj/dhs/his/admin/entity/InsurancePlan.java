package gov.nj.dhs.his.admin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "PLAN_MASTER")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PLAN_ID")
    private Long planId;

    @NotBlank(message = "Plan Name is mandatory")
    @Size(min = 3, max = 50, message = "Plan Name must be between 3 and 50 characters")
    @Column(name = "PLAN_NAME", nullable = false, unique = true)
    private String planName;

    @NotNull(message = "Plan Status is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "PLAN_STATUS")
    private PlanStatus planStatus;

    @NotNull(message = "Start Date is mandatory")
    @Column(name = "START_DATE")
    private LocalDate startDate;

    @NotNull(message = "End Date is mandatory")
    @Column(name = "END_DATE")
    private LocalDate endDate;

    @Column(name = "CREATED_DATE", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(name = "UPDATED_DATE")
    @UpdateTimestamp
    private LocalDateTime updatedDate;
}
