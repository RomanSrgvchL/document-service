package com.group.itq.dto.request;

import com.group.itq.util.Messages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitRequestDto {

    @NotNull(message = Messages.ID_REQUIRED)
    @Size(min = 1, max = 1000, message = Messages.ID_LIST_INVALID)
    private List<Long> ids;

    @NotBlank(message = Messages.INITIATOR_REQUIRED)
    @Size(max = 30, message = Messages.INITIATOR_MAX_LENGTH)
    private String initiator;

    @Size(max = 500, message = Messages.COMMENT_MAX_LENGTH)
    private String comment;
}
