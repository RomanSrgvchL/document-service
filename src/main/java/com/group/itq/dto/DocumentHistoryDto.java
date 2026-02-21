package com.group.itq.dto;

import com.group.itq.model.DocumentHistoryAction;
import com.group.itq.util.Messages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class DocumentHistoryDto {

    private Long id;

    @NotBlank(message = Messages.INITIATOR_REQUIRED)
    @Size(max = 30, message = Messages.INITIATOR_MAX_LENGTH)
    private String initiator;

    @Size(max = 500, message = Messages.COMMENT_MAX_LENGTH)
    private String comment;

    @NotNull(message = Messages.STATUS_REQUIRED)
    private DocumentHistoryAction action;

    private ZonedDateTime createdAt;
}
