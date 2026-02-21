package com.group.itq.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConcurrencyTestResponse {

    private int totalAttempts;
    private int successful;
    private int conflicts;
    private int registryErrors;
    private int notFound;
    private String finalStatus;
    private boolean registryEntryCreated;
}