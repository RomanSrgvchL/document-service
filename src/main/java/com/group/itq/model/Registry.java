package com.group.itq.model;

import com.group.itq.util.Messages;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "registries")
public class Registry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", unique = true, nullable = false)
    private Document document;

    @NotBlank(message = Messages.APPROVER_REQUIRED)
    @Size(max = 30, message = Messages.APPROVER_MAX_LENGTH)
    @Column(name = "approver", length = 30, nullable = false)
    private String approver;

    @CreationTimestamp
    @Column(name = "approved_at", nullable = false, updatable = false)
    private ZonedDateTime approvedAt;
}
