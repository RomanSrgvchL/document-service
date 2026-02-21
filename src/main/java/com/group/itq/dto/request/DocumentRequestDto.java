package com.group.itq.dto.request;

import com.group.itq.util.Messages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRequestDto {

    @NotBlank(message = Messages.AUTHOR_REQUIRED)
    @Size(max = 100, message = Messages.AUTHOR_MAX_LENGTH)
    private String author;

    @NotBlank(message = Messages.NAME_REQUIRED)
    @Size(max = 200, message = Messages.NAME_MAX_LENGTH)
    private String name;
}
