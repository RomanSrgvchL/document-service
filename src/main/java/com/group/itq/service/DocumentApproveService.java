package com.group.itq.service;

import com.group.itq.dto.response.ApproveResponseDto;
import com.group.itq.enums.ApproveStatus;
import com.group.itq.model.Document;
import com.group.itq.model.DocumentHistory;
import com.group.itq.model.DocumentHistoryAction;
import com.group.itq.model.DocumentStatus;
import com.group.itq.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentApproveService {

    private final RegistryService registryService;
    private final DocumentRepository documentRepository;

    @Transactional
    public ApproveResponseDto approveDocument(Long id, String approver, String comment) {
        try {
            log.info("Начало утверждения документа ID={}, утверждающий={}", id, approver);

            Document document = documentRepository.findByIdWithHistory(id).orElse(null);

            if (document == null) {
                return ApproveResponseDto.builder()
                        .id(id)
                        .status(ApproveStatus.NOT_FOUND)
                        .message("Документ не найден")
                        .build();
            }

            if (document.getStatus() != DocumentStatus.SUBMITTED) {
                return ApproveResponseDto.builder()
                        .id(id)
                        .status(ApproveStatus.CONFLICT)
                        .message("Документ в статусе %s, ожидался SUBMITTED".formatted(document.getStatus()))
                        .build();
            }

            boolean registered = registryService.registerApproval(document, approver);

            if (!registered) {
                log.warn("Не удалось зарегистрировать документ ID={} в реестре (возможно уже зарегистрирован)", id);

                return ApproveResponseDto.builder()
                        .id(id)
                        .status(ApproveStatus.CONFLICT)
                        .message("Не удалось зарегистрировать документ в реестре")
                        .build();
            }

            document.setStatus(DocumentStatus.APPROVED);

            DocumentHistory history = DocumentHistory.builder()
                    .document(document)
                    .action(DocumentHistoryAction.APPROVE)
                    .initiator(approver)
                    .comment(comment)
                    .build();

            if (document.getHistory() == null) {
                document.setHistory(new ArrayList<>());
            }
            document.getHistory().add(history);

            documentRepository.save(document);

            log.info("Документ ID={} успешно утвержден", id);

            return ApproveResponseDto.builder()
                    .id(id)
                    .status(ApproveStatus.SUCCESS)
                    .message("Документ успешно утвержден")
                    .build();
        } catch (Exception e) {
            log.error("Возникла ошибка при регистрации документа: {}", e.getMessage());

            return ApproveResponseDto.builder()
                    .id(id)
                    .status(ApproveStatus.REGISTRY_ERROR)
                    .message("Возникла ошибка при регистрации документа")
                    .build();
        }
    }
}
