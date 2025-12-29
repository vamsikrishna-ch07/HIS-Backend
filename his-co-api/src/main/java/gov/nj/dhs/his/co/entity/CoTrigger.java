package gov.nj.dhs.his.co.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("CO_TRIGGERS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoTrigger {

    @Id
    @Column("TRIGGER_ID")
    private Long triggerId;

    @Column("APP_ID")
    private Long appId;

    @Column("NOTICE_PDF")
    private byte[] noticePdf;

    @Column("TRIGGER_STATUS")
    private String triggerStatus; // PENDING, COMPLETED, FAILED

    @Column("FAILURE_REASON")
    private String failureReason;

    @CreatedDate
    @Column("CREATED_DATE")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column("UPDATED_DATE")
    private LocalDateTime updatedDate;
}
