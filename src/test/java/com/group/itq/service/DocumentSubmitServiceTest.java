package com.group.itq.service;

import com.group.itq.base.IntegrationTestBase;
import com.group.itq.dto.DocumentWithHistoryDto;
import com.group.itq.dto.request.SubmitRequestDto;
import com.group.itq.dto.response.SubmitResponseDto;
import com.group.itq.enums.SubmitStatus;
import com.group.itq.model.Document;
import com.group.itq.model.DocumentStatus;
import com.group.itq.repository.DocumentRepository;
import com.group.itq.util.DataFactoryTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentSubmitServiceTest extends IntegrationTestBase {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DataFactoryTest dataFactoryTest;

    @AfterEach
    void clean() {
        documentRepository.deleteAll();
    }

    @Test
    void submitDocuments_allDraftDocuments_shouldAllSucceed() {
        List<Long> draftIds = dataFactoryTest.createMultipleDraftDocuments(3, "submit-test");

        SubmitRequestDto request = SubmitRequestDto.builder()
                .ids(draftIds)
                .initiator("test-initiator")
                .comment("test submit all")
                .build();

        List<SubmitResponseDto> results = documentService.submitDocuments(request);

        assertThat(results).hasSize(3);
        assertThat(results).allSatisfy(r -> {
            assertThat(r.getStatus()).isEqualTo(SubmitStatus.SUCCESS);
            assertThat(r.getMessage()).isNotBlank();
        });

        draftIds.forEach(id -> {
            DocumentWithHistoryDto docWithHistory= documentService.findById(id);
            assertThat(docWithHistory.getStatus()).isEqualTo(DocumentStatus.SUBMITTED);
        });
    }

    @Test
    void submitDocuments_mixedIds_shouldReturnPartialResults() {
        Document draft1 = dataFactoryTest.createDraftDocument("author1", "doc1");
        Document draft2 = dataFactoryTest.createDraftDocument("author2", "doc2");
        Document submitted = dataFactoryTest.createSubmittedDocument("author3", "doc3");
        Document approved = dataFactoryTest.createApprovedDocument("author4", "doc4");

        List<Long> ids = List.of(
                draft1.getId(),
                draft2.getId(),
                submitted.getId(),
                approved.getId(),
                99999L
        );

        SubmitRequestDto request = SubmitRequestDto.builder()
                .ids(ids)
                .initiator("test-initiator")
                .comment("test submit mixed")
                .build();

        List<SubmitResponseDto> results = documentService.submitDocuments(request);

        assertThat(results).hasSize(5);

        assertThat(results.get(0).getStatus()).isEqualTo(SubmitStatus.SUCCESS);
        assertThat(results.get(1).getStatus()).isEqualTo(SubmitStatus.SUCCESS);

        assertThat(results.get(2).getStatus()).isEqualTo(SubmitStatus.CONFLICT);
        assertThat(results.get(3).getStatus()).isEqualTo(SubmitStatus.CONFLICT);

        assertThat(results.get(4).getStatus()).isEqualTo(SubmitStatus.NOT_FOUND);

        assertThat(documentService.findById(draft1.getId()).getStatus()).isEqualTo(DocumentStatus.SUBMITTED);
        assertThat(documentService.findById(draft2.getId()).getStatus()).isEqualTo(DocumentStatus.SUBMITTED);
        assertThat(documentService.findById(submitted.getId()).getStatus()).isEqualTo(DocumentStatus.SUBMITTED);
        assertThat(documentService.findById(approved.getId()).getStatus()).isEqualTo(DocumentStatus.APPROVED);
    }

    @Test
    void submitDocuments_emptyList_shouldReturnEmptyList() {
        SubmitRequestDto request = SubmitRequestDto.builder()
                .ids(List.of())
                .initiator("test-initiator")
                .build();

        List<SubmitResponseDto> results = documentService.submitDocuments(request);

        assertThat(results).isEmpty();
    }

    @Test
    void submitDocuments_duplicateIds_shouldProcessEach() {
        Document draft = dataFactoryTest.createDraftDocument("author", "doc");
        List<Long> ids = List.of(draft.getId(), draft.getId());

        SubmitRequestDto request = SubmitRequestDto.builder()
                .ids(ids)
                .initiator("test-initiator")
                .build();

        List<SubmitResponseDto> results = documentService.submitDocuments(request);

        assertThat(results).hasSize(2);

        assertThat(results.get(0).getStatus()).isEqualTo(SubmitStatus.SUCCESS);

        assertThat(results.get(1).getStatus()).isEqualTo(SubmitStatus.CONFLICT);
    }

    @Test
    void submitDocuments_nullComment_shouldWork() {
        Document draft = dataFactoryTest.createDraftDocument("author", "doc");

        SubmitRequestDto request = SubmitRequestDto.builder()
                .ids(List.of(draft.getId()))
                .initiator("test-initiator")
                .comment(null)
                .build();

        List<SubmitResponseDto> results = documentService.submitDocuments(request);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getStatus()).isEqualTo(SubmitStatus.SUCCESS);
    }
}
