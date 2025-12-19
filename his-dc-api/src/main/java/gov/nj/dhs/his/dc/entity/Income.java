package gov.nj.dhs.his.dc.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DC_INCOME")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INCOME_ID")
    private Long incomeId;

    @Column(name = "SALARY_INCOME")
    private Double salaryIncome;

    @Column(name = "RENT_INCOME")
    private Double rentIncome;

    @Column(name = "PROPERTY_INCOME")
    private Double propertyIncome;

    @OneToOne
    @JoinColumn(name = "case_id", nullable = false)
    private DcCase dcCase;
}