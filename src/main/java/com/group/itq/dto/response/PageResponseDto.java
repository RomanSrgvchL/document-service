package com.group.itq.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PageResponseDto<T> {

    private List<T> content;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private boolean isLast;
}
