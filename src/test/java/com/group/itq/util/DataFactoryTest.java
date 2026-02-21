package com.group.itq.util;

import com.group.itq.dto.request.DocumentRequestDto;
import com.group.itq.model.Document;
import com.group.itq.model.DocumentStatus;
import com.group.itq.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataFactoryTest {

    private final DocumentRepository documentRepository;

    public DocumentRequestDto createDocumentRequest(String author, String name) {
        return DocumentRequestDto.builder()
                .author(author)
                .name(name)
                .build();
    }

    public Document createDraftDocument(String author, String name) {
        Document document = Document.builder()
                .author(author)
                .name(name)
                .documentNumber(UUID.randomUUID().toString())
                .status(DocumentStatus.DRAFT)
                .history(new ArrayList<>())
                .build();
        return documentRepository.save(document);
    }

    public Document createSubmittedDocument(String author, String name) {
        Document document = Document.builder()
                .author(author)
                .name(name)
                .documentNumber(UUID.randomUUID().toString())
                .status(DocumentStatus.SUBMITTED)
                .history(new ArrayList<>())
                .build();
        return documentRepository.save(document);
    }

    public List<Long> createMultipleDraftDocuments(int count, String authorPrefix) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Document doc = createDraftDocument(authorPrefix + i, "Document " + i);
            ids.add(doc.getId());
        }
        return ids;
    }

    public Document createApprovedDocument(String author, String name) {
        Document document = Document.builder()
                .author(author)
                .name(name)
                .documentNumber(UUID.randomUUID().toString())
                .status(DocumentStatus.APPROVED)
                .history(new ArrayList<>())
                .build();
        return documentRepository.save(document);
    }
}
