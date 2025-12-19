package gov.nj.dhs.his.dc.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DC_KIDS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Kid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KID_ID")
    private Long kidId;

    @Column(name = "KID_NAME")
    private String kidName;

    @Column(name = "KID_AGE")
    private Integer kidAge;

    @Column(name = "KID_SSN")
    private Long kidSsn;

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    private DcCase dcCase;
}