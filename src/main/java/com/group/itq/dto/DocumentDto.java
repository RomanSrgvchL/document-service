package com.group.itq.dto;

import com.group.itq.model.DocumentStatus;
import com.group.itq.util.Messages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DocumentDto {

    @NotNull(message = Messages.ID_REQUIRED)
    private Long id;

    @NotBlank(message = Messages.DOCUMENT_NUMBER_MAX_LENGTH)
    @Size(max = 36, message = Messages.DOCUMENT_NUMBER_MAX_LENGTH)
    private String documentNumber;

    @NotBlank(message = Messages.AUTHOR_REQUIRED)
    @Size(max = 100, message = Messages.AUTHOR_MAX_LENGTH)
    private String author;

    @NotNull(message = Messages.STATUS_REQUIRED)
    private DocumentStatus status;

    @NotBlank(message = Messages.NAME_REQUIRED)
    @Size(max = 200, message = Messages.NAME_MAX_LENGTH)
    private String name;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;
}
