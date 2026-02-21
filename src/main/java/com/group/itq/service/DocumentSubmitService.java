package com.group.itq.service;

import com.group.itq.dto.response.SubmitResponseDto;
import com.group.itq.enums.SubmitStatus;
import com.group.itq.model.Document;
import com.group.itq.model.DocumentHistory;
import com.group.itq.model.DocumentHistoryAction;
import com.group.itq.model.DocumentStatus;
import com.group.itq.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentSubmitService {

    private final DocumentRepository documentRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SubmitResponseDto submitDocument(Long id, String initiator, String comment) {
        log.info("Начало отправки документа ID={}, инициатор={}", id, initiator);

        try {
            Document document = documentRepository.findById(id).orElse(null);

            if (document == null) {
                log.warn("Документ ID={} не найден", id);

                return SubmitResponseDto.builder()
                        .id(id)
                        .status(SubmitStatus.NOT_FOUND)
                        .message("Документ не найден")
                        .build();
            }

            if (document.getStatus() != DocumentStatus.DRAFT) {
                log.warn("Документ ID={} в статусе {}, требуется DRAFT", id, document.getStatus());

                return SubmitResponseDto.builder()
                        .id(id)
                        .status(SubmitStatus.CONFLICT)
                        .message("Документ уже обработан")
                        .build();
            }

            document.setStatus(DocumentStatus.SUBMITTED);

            DocumentHistory history = DocumentHistory.builder()
                    .document(document)
                    .action(DocumentHistoryAction.SUBMIT)
                    .initiator(initiator)
                    .comment(comment)
                    .build();

            document.getHistory().add(history);

            documentRepository.save(document);

            log.info("Документ ID={} успешно отправлен на согласование", id);

            return SubmitResponseDto.builder()
                    .id(id)
                    .status(SubmitStatus.SUCCESS)
                    .message("Документ успешно обработан")
                    .build();

        } catch (Exception e) {
            log.error("Ошибка при отправке документа ID={}: {}", id, e.getMessage(), e);

            return SubmitResponseDto.builder()
                    .id(id)
                    .status(SubmitStatus.CONFLICT)
                    .message("Возникла ошибка при обработке документа")
                    .build();
        }
    }
}
