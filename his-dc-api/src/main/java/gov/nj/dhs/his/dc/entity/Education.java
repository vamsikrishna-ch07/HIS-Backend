package gov.nj.dhs.his.dc.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DC_EDUCATION")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EDUCATION_ID")
    private Long educationId;

    @Column(name = "HIGHEST_DEGREE")
    private String highestDegree;

    @Column(name = "GRADUATION_YEAR")
    private Integer graduationYear;

    @Column(name = "UNIVERSITY_NAME")
    private String universityName;

    @OneToOne
    @JoinColumn(name = "case_id", nullable = false)
    private DcCase dcCase;
}