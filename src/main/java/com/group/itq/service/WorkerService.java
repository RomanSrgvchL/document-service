package com.group.itq.service;

import com.group.itq.dto.request.ApproveRequestDto;
import com.group.itq.dto.request.SubmitRequestDto;
import com.group.itq.dto.response.ApproveResponseDto;
import com.group.itq.dto.response.SubmitResponseDto;
import com.group.itq.enums.ApproveStatus;
import com.group.itq.enums.SubmitStatus;
import com.group.itq.model.DocumentStatus;
import com.group.itq.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerService {

    private final DocumentRepository documentRepository;
    private final DocumentService documentService;

    @Value("${worker.submit.batch-size:100}")
    private int submitBatchSize;

    @Value("${worker.approve.batch-size:100}")
    private int approveBatchSize;

    @Scheduled(fixedDelayString = "${worker.submit.fixed-delay:60000}")
    public void processDraftDocuments() {
        log.info("SUBMIT вокрер начал работу");
        Instant start = Instant.now();

        try {
            long totalDraft = documentRepository.countByStatus(DocumentStatus.DRAFT);

            List<Long> draftIds = documentRepository.findIdsByStatus(
                    DocumentStatus.DRAFT,
                    PageRequest.of(0, submitBatchSize)
            );

            if (draftIds.isEmpty()) {
                log.info("Не найден ни один DRAFT документ");
                return;
            }

            log.info("Статистика: всего DRAFT = {}, обрабатываем пачку из {}", totalDraft, draftIds.size());

            SubmitRequestDto request = SubmitRequestDto.builder()
                    .ids(draftIds)
                    .initiator("system-submit-worker")
                    .comment("Автоматическая отправка на согласование")
                    .build();

            List<SubmitResponseDto> results = documentService.submitDocuments(request);

            long success = results.stream()
                    .filter(r -> r.getStatus() == SubmitStatus.SUCCESS)
                    .count();
            long conflict = results.stream()
                    .filter(r -> r.getStatus() == SubmitStatus.CONFLICT)
                    .count();
            long notFound = results.stream()
                    .filter(r -> r.getStatus() == SubmitStatus.NOT_FOUND)
                    .count();

            long remainingDraft = documentRepository.countByStatus(DocumentStatus.DRAFT);
            long processed = totalDraft - remainingDraft;

            Duration duration = Duration.between(start, Instant.now());

            log.info("Обработано DRAFT: {} из {} (осталось {})", processed, totalDraft, remainingDraft);
            log.info("Результат: SUCCESS={}, CONFLICT={}, NOT_FOUND={}, TOTAL={}",
                    success, conflict, notFound, results.size());
            log.info("SUBMIT воркер завершён за {} мс", duration.toMillis());
        } catch (Exception e) {
            log.error("В SUBMIT воркере возникла ошибка: {}", e.getMessage(), e);
        }

        log.info("SUBMIT вокрер завершил работу");
    }

    @Scheduled(fixedDelayString = "${worker.approve.fixed-delay:60000}")
    public void processSubmittedDocuments() {
        log.info("APPROVE вокрер начал работу");
        Instant start = Instant.now();

        try {
            long totalSubmitted = documentRepository.countByStatus(DocumentStatus.SUBMITTED);

            List<Long> submittedIds = documentRepository.findIdsByStatus(
                    DocumentStatus.SUBMITTED,
                    PageRequest.of(0, approveBatchSize)
            );

            if (submittedIds.isEmpty()) {
                log.info("Нет SUBMITTED документов для обработки");
                return;
            }

            log.info("Статистика: всего SUBMITTED = {}, обрабатываем пачку из {}",
                    totalSubmitted, submittedIds.size());

            ApproveRequestDto request = ApproveRequestDto.builder()
                    .ids(submittedIds)
                    .approver("system-approve-worker")
                    .comment("Автоматическое утверждение")
                    .build();

            List<ApproveResponseDto> results = documentService.approveDocuments(request);

            long success = results.stream()
                    .filter(r -> r.getStatus() == ApproveStatus.SUCCESS)
                    .count();
            long registryError = results.stream()
                    .filter(r -> r.getStatus() == ApproveStatus.REGISTRY_ERROR)
                    .count();
            long conflict = results.stream()
                    .filter(r -> r.getStatus() == ApproveStatus.CONFLICT)
                    .count();
            long notFound = results.stream()
                    .filter(r -> r.getStatus() == ApproveStatus.NOT_FOUND)
                    .count();

            long remainingSubmitted = documentRepository.countByStatus(DocumentStatus.SUBMITTED);
            long processed = totalSubmitted - remainingSubmitted;

            Duration duration = Duration.between(start, Instant.now());
            log.info("Обработано SUBMITTED: {} из {} (осталось {})",
                    processed, totalSubmitted, remainingSubmitted);
            log.info("Результаты пачки: SUCCESS={}, REGISTRY_ERROR={}, CONFLICT={}, NOT_FOUND={}",
                    success, registryError, conflict, notFound);
            log.info("Время выполнения: {} мс", duration.toMillis());

        } catch (Exception e) {
            log.error("КРИТИЧЕСКАЯ ОШИБКА в APPROVE воркере: {}", e.getMessage(), e);
        }

        log.info("APPROVE вокрер завершил работу");
    }
}
