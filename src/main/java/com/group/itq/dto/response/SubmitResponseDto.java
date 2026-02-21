package com.group.itq.dto.response;

import com.group.itq.enums.SubmitStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitResponseDto {

    private Long id;
    private SubmitStatus status;
    private String message;
}
