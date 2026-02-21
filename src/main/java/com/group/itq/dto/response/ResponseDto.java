package com.group.itq.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {
    private boolean success;
    private String message;

    public static ResponseDto buildFailure(String message) {
        return new ResponseDto(false, message);
    }

    public static ResponseDto buildSuccess(String message) {
        return new ResponseDto(true, message);
    }
}
