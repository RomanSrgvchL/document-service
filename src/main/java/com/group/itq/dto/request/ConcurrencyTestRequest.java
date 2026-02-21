package com.group.itq.dto.request;

import com.group.itq.util.Messages;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcurrencyTestRequest {

    @NotNull(message = Messages.ID_REQUIRED)
    private Long documentId;

    @Min(value = 1, message = Messages.THREADS_MIN)
    @Max(value = 5, message = Messages.THREADS_MAX)
    private int threads;

    @Min(value = 1, message = Messages.ATTEMPTS_MIN)
    @Max(value = 100, message = Messages.ATTEMPTS_MAX)
    private int attempts;

    @Size(max = 30, message = Messages.APPROVER_MAX_LENGTH)
    private String approver = "concurrency-tester";
}
