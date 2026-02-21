package com.group.itq.service;

import com.group.itq.base.IntegrationTestBase;
import com.group.itq.dto.DocumentDto;
import com.group.itq.dto.DocumentWithHistoryDto;
import com.group.itq.dto.request.DocumentRequestDto;
import com.group.itq.enums.DocumentSortFields;
import com.group.itq.model.Document;
import com.group.itq.model.DocumentStatus;
import com.group.itq.util.DataFactoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class DocumentServiceTest extends IntegrationTestBase {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DataFactoryTest dataFactoryTest;

    @Test
    public void save_shouldCreateDraftDocument() {
        String author = "test-author";
        String documentName = "test-document-name";

        DocumentRequestDto request = dataFactoryTest.createDocumentRequest(author, documentName);

        DocumentDto result = documentService.save(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isPositive();
        assertThat(result.getAuthor()).isEqualTo(author);
        assertThat(result.getName()).isEqualTo(documentName);
        assertThat(result.getStatus()).isEqualTo(DocumentStatus.DRAFT);
        assertThat(result.getDocumentNumber()).isNotBlank();
    }

    @Test
    public void findById_shouldReturnDocumentWithHistory() {
        String author = "test-author";
        String documentName = "test-document-name";

        Document document = dataFactoryTest.createDraftDocument(author, documentName);

        DocumentWithHistoryDto result = documentService.findById(document.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(document.getId());
        assertThat(result.getAuthor()).isEqualTo(author);
        assertThat(result.getHistory()).isNotNull();
    }

    @Test
    public void findAll_shouldReturnPagedResults() {
        String author = "test-author";

        dataFactoryTest.createMultipleDraftDocuments(5, author);

        var result = documentService.findAll(Sort.by(DocumentSortFields.ID.getFieldName()), 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getTotalElements()).isEqualTo(5);
    }
}
