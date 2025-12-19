package gov.nj.dhs.his.co.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "CO_TRIGGERS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoTrigger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRIGGER_ID")
    private Long triggerId;

    @Column(name = "APP_ID", nullable = false)
    private Long appId;

    @Lob
    @Column(name = "NOTICE_PDF")
    private byte[] noticePdf;

    @Column(name = "TRIGGER_STATUS")
    private String triggerStatus; // PENDING, COMPLETED, FAILED

    @Column(name = "FAILURE_REASON")
    private String failureReason;

    @Column(name = "CREATED_DATE", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(name = "UPDATED_DATE")
    @UpdateTimestamp
    private LocalDateTime updatedDate;
}