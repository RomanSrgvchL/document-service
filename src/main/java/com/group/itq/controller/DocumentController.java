package com.group.itq.controller;

import com.group.itq.dto.DocumentDto;
import com.group.itq.dto.request.*;
import com.group.itq.dto.response.ApproveResponseDto;
import com.group.itq.dto.response.ConcurrencyTestResponse;
import com.group.itq.dto.response.SubmitResponseDto;
import com.group.itq.dto.DocumentWithHistoryDto;
import com.group.itq.dto.response.PageResponseDto;
import com.group.itq.enums.DocumentSortFields;
import com.group.itq.enums.SortOrder;
import com.group.itq.model.DocumentStatus;
import com.group.itq.service.ConcurrencyApprovalService;
import com.group.itq.service.DocumentService;
import com.group.itq.util.Messages;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@Validated
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final ConcurrencyApprovalService concurrencyApprovalService;
    private static final int MAX_BATCH_SIZE = 1000;

    @GetMapping
    public ResponseEntity<PageResponseDto<DocumentDto>> getAll(
            @RequestParam(value = "sort", defaultValue = "CREATED_AT") DocumentSortFields sort,
            @RequestParam(value = "order", defaultValue = "DESC") SortOrder order,
            @RequestParam(value = "page", defaultValue = "0")
            @PositiveOrZero(message = Messages.PAGE_POSITIVE_OR_ZERO) int page,
            @RequestParam(value = "size", defaultValue = "10")
            @Positive(message = Messages.SIZE_POSITIVE) int size) {

        PageResponseDto<DocumentDto> pageResponseDto = documentService.findAll(Sort.by(order.getDirection(),
                sort.getFieldName()), page, size);

        return ResponseEntity.ok(pageResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentWithHistoryDto> getById(
            @PathVariable @Positive(message = Messages.ID_POSITIVE) long id) {
        DocumentWithHistoryDto documentWithHistoryDto = documentService.findById(id);
        return ResponseEntity.ok(documentWithHistoryDto);
    }

    @GetMapping("/batch")
    public ResponseEntity<List<DocumentDto>> getBatch(
            @RequestParam("ids") List<@Positive(message = Messages.ID_POSITIVE) Long> ids) {

        if (ids.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        if (ids.size() > MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Максимально возможный размер пакета - 1000 элементов");
        }

        return ResponseEntity.ok(documentService.findAllByIds(ids));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<DocumentDto>> search(
            @RequestParam(required = false) DocumentStatus status,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateTo,
            @RequestParam(value = "sort", defaultValue = "CREATED_AT") DocumentSortFields sort,
            @RequestParam(value = "order", defaultValue = "DESC") SortOrder order,
            @RequestParam(value = "page", defaultValue = "0")
            @PositiveOrZero(message = Messages.PAGE_POSITIVE_OR_ZERO) int page,
            @RequestParam(value = "size", defaultValue = "10")
            @Positive(message = Messages.SIZE_POSITIVE) int size) {

        PageResponseDto<DocumentDto> result = documentService.search(status, author, dateFrom, dateTo,
                Sort.by(order.getDirection(), sort.getFieldName()), page, size);

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/submit")
    public ResponseEntity<List<SubmitResponseDto>> submit(@Valid @RequestBody SubmitRequestDto request) {
        return ResponseEntity.ok(documentService.submitDocuments(request));
    }

    @PatchMapping("/approve")
    public ResponseEntity<List<ApproveResponseDto>> approve(
            @Valid @RequestBody ApproveRequestDto request) {
        return ResponseEntity.ok(documentService.approveDocuments(request));
    }

    @PostMapping
    public ResponseEntity<DocumentDto> create(@RequestBody @Valid DocumentRequestDto request) {
        DocumentDto documentDto = documentService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(documentDto);
    }

    @PostMapping("/test-concurrency")
    public ResponseEntity<ConcurrencyTestResponse> testConcurrency(
            @Valid @RequestBody ConcurrencyTestRequest request) {
        return ResponseEntity.ok(concurrencyApprovalService.testConcurrentApproval(request));
    }
}
