package com.group.itq.model;

import com.group.itq.util.Messages;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = Messages.AUTHOR_REQUIRED)
    @Size(max = 100, message = Messages.AUTHOR_MAX_LENGTH)
    @Column(name = "author", length = 100, nullable = false)
    private String author;

    @NotBlank(message = Messages.NAME_REQUIRED)
    @Size(max = 200, message = Messages.NAME_MAX_LENGTH)
    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @NotBlank(message = Messages.DOCUMENT_NUMBER_MAX_LENGTH)
    @Size(max = 36, message = Messages.DOCUMENT_NUMBER_MAX_LENGTH)
    @Column(name = "document_number", length = 30, unique = true, nullable = false)
    private String documentNumber;

    @NotNull(message = Messages.STATUS_REQUIRED)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DocumentStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private List<DocumentHistory> history;
}
