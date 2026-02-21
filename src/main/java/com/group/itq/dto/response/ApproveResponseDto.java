package com.group.itq.dto.response;

import com.group.itq.enums.ApproveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveResponseDto {

    private Long id;
    private ApproveStatus status;
    private String message;
}