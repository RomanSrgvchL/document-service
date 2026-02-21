package com.group.itq.service;

import com.group.itq.base.IntegrationTestBase;
import com.group.itq.dto.request.ApproveRequestDto;
import com.group.itq.dto.request.ConcurrencyTestRequest;
import com.group.itq.dto.response.ApproveResponseDto;
import com.group.itq.dto.response.ConcurrencyTestResponse;
import com.group.itq.enums.ApproveStatus;
import com.group.itq.model.Document;
import com.group.itq.model.DocumentStatus;
import com.group.itq.model.Registry;
import com.group.itq.repository.DocumentRepository;
import com.group.itq.repository.RegistryRepository;
import com.group.itq.util.DataFactoryTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentApproveServiceTest extends IntegrationTestBase {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ConcurrencyApprovalService concurrencyApprovalService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private RegistryRepository registryRepository;

    @Autowired
    private DataFactoryTest dataFactoryTest;

    @AfterEach
    void clean() {
        registryRepository.deleteAll();
        documentRepository.deleteAll();
    }

    @Test
    void approveDocuments_partialSuccess_shouldReturnMixedStatuses() {
        Document submitted = dataFactoryTest.createSubmittedDocument("author1", "doc1");
        Document approved = dataFactoryTest.createApprovedDocument("author2", "doc2");

        ApproveRequestDto request = ApproveRequestDto.builder()
                .ids(List.of(submitted.getId(), approved.getId()))
                .approver("approver")
                .comment("approve batch")
                .build();

        List<ApproveResponseDto> results = documentService.approveDocuments(request);

        assertThat(results).hasSize(2);

        assertThat(results.get(0).getStatus()).isEqualTo(ApproveStatus.SUCCESS);

        assertThat(results.get(1).getStatus()).isEqualTo(ApproveStatus.CONFLICT);

        Document updated = documentRepository.findById(submitted.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(DocumentStatus.APPROVED);

        assertThat(registryRepository.existsByDocumentId(submitted.getId())).isTrue();
    }

    @Test
    void approveDocument_registryError_shouldNotChangeStatus() {
        Document submitted = dataFactoryTest.createSubmittedDocument("author", "doc");

        registryRepository.save(
                Registry.builder()
                        .document(submitted)
                        .approver("someone")
                        .build()
        );

        ApproveRequestDto request = ApproveRequestDto.builder()
                .ids(List.of(submitted.getId()))
                .approver("approver")
                .comment("approve")
                .build();

        List<ApproveResponseDto> result = documentService.approveDocuments(request);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStatus())
                .isEqualTo(ApproveStatus.CONFLICT);

        Document reloaded = documentRepository.findById(submitted.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(DocumentStatus.SUBMITTED);
    }

    @Test
    void concurrentApprove_onlyOneSuccess() {
        Document submitted = dataFactoryTest.createSubmittedDocument("author", "doc");

        ConcurrencyTestRequest request = ConcurrencyTestRequest.builder()
                .documentId(submitted.getId())
                .approver("approver")
                .threads(5)
                .attempts(10)
                .build();

        ConcurrencyTestResponse response = concurrencyApprovalService.testConcurrentApproval(request);


        assertThat(response.getSuccessful()).isEqualTo(1);
        assertThat(response.getConflicts() + response.getRegistryErrors())
                .isEqualTo(request.getAttempts() - 1);

        Document finalDoc = documentRepository.findById(submitted.getId()).orElseThrow();
        assertThat(finalDoc.getStatus()).isEqualTo(DocumentStatus.APPROVED);

        assertThat(registryRepository.existsByDocumentId(submitted.getId())).isTrue();
    }
}