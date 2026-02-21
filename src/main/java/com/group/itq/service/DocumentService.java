package com.group.itq.service;

import com.group.itq.dto.DocumentDto;
import com.group.itq.dto.request.ApproveRequestDto;
import com.group.itq.dto.response.ApproveResponseDto;
import com.group.itq.dto.response.SubmitResponseDto;
import com.group.itq.dto.request.DocumentRequestDto;
import com.group.itq.dto.DocumentWithHistoryDto;
import com.group.itq.dto.request.SubmitRequestDto;
import com.group.itq.dto.response.PageResponseDto;
import com.group.itq.enums.ApproveStatus;
import com.group.itq.exception.ResourceNotFoundException;
import com.group.itq.mapper.DocumentMapper;
import com.group.itq.model.Document;
import com.group.itq.model.DocumentHistory;
import com.group.itq.model.DocumentHistoryAction;
import com.group.itq.model.DocumentStatus;
import com.group.itq.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentApproveService documentApproveService;
    private final DocumentRepository documentRepository;
    private final DocumentSubmitService documentSubmitService;
    private final DocumentMapper documentMapper;

    @Transactional(readOnly = true)
    public PageResponseDto<DocumentDto> findAll(Sort sort, int page, int size) {
        Page<DocumentDto> documentsPage = documentRepository.findAll(PageRequest.of(page, size, sort))
                .map(documentMapper::documentToDocumentDto);

        return PageResponseDto.<DocumentDto>builder()
                .content(documentsPage.getContent())
                .page(page)
                .size(size)
                .totalPages(documentsPage.getTotalPages())
                .totalElements(documentsPage.getTotalElements())
                .isLast(documentsPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public DocumentWithHistoryDto findById(long id) {
        Document document = documentRepository.findByIdWithHistory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Документ с указанным ID не найден"));

        return documentMapper.documentToDocumentWithHistoryDto(document);
    }

    @Transactional(readOnly = true)
    public List<DocumentDto> findAllByIds(List<Long> ids) {
        return documentRepository.findAllById(ids)
                .stream()
                .map(documentMapper::documentToDocumentDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponseDto<DocumentDto> search(DocumentStatus status, String author, ZonedDateTime dateFrom,
            ZonedDateTime dateTo, Sort sort, int page, int size) {

        Page<Document> documentsPage = documentRepository.search(status, author, dateFrom,
                dateTo, PageRequest.of(page, size, sort));

        Page<DocumentDto> dtoPage = documentsPage.map(documentMapper::documentToDocumentDto);

        return PageResponseDto.<DocumentDto>builder()
                .content(dtoPage.getContent())
                .page(page)
                .size(size)
                .totalPages(dtoPage.getTotalPages())
                .totalElements(dtoPage.getTotalElements())
                .isLast(dtoPage.isLast())
                .build();
    }

    @Transactional
    public DocumentDto save(DocumentRequestDto request) {
        Document document = Document.builder()
                .documentNumber(UUID.randomUUID().toString())
                .author(request.getAuthor())
                .name(request.getName())
                .status(DocumentStatus.DRAFT)
                .build();

        return documentMapper.documentToDocumentDto(documentRepository.save(document));
    }

    @Transactional
    public List<SubmitResponseDto> submitDocuments(SubmitRequestDto request) {
        log.info("Отправка документов на согласование: инициатор={}, количество ID={}",
                request.getInitiator(), request.getIds().size());

        List<SubmitResponseDto> results = new ArrayList<>();

        request.getIds().forEach(id ->
            results.add(documentSubmitService.submitDocument(id, request.getInitiator(), request.getComment()))
        );

        return results;
    }

    @Transactional
    public List<ApproveResponseDto> approveDocuments(ApproveRequestDto request) {
        log.info("Утверждение документов: утверждающий={}, количество ID={}",
                request.getApprover(), request.getIds().size());

        List<ApproveResponseDto> results = new ArrayList<>();

        for (Long id : request.getIds()) {
            results.add(documentApproveService.approveDocument(id, request.getApprover(), request.getComment()));
        }

        return results;
    }
}
