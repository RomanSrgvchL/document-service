package com.group.itq.model;

import com.group.itq.util.Messages;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"document"})
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "document_history")
public class DocumentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = Messages.INITIATOR_REQUIRED)
    @Size(max = 30, message = Messages.INITIATOR_MAX_LENGTH)
    @Column(name = "initiator", length = 30, nullable = false)
    private String initiator;

    @Size(max = 500, message = Messages.COMMENT_MAX_LENGTH)
    @Column(name = "comment", length = 500)
    private String comment;

    @NotNull(message = Messages.STATUS_REQUIRED)
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private DocumentHistoryAction action;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", referencedColumnName = "id", nullable = false)
    private Document document;
}
