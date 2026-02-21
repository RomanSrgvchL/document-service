package com.group.itq.service;

import com.group.itq.dto.request.ApproveRequestDto;
import com.group.itq.dto.request.ConcurrencyTestRequest;
import com.group.itq.dto.response.ApproveResponseDto;
import com.group.itq.dto.response.ConcurrencyTestResponse;
import com.group.itq.exception.ResourceNotFoundException;
import com.group.itq.model.Document;
import com.group.itq.model.DocumentStatus;
import com.group.itq.repository.DocumentRepository;
import com.group.itq.repository.RegistryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConcurrencyApprovalService {

    private final DocumentRepository documentRepository;
    private final RegistryRepository registryRepository;
    private final DocumentService documentService;

    public ConcurrencyTestResponse testConcurrentApproval(ConcurrencyTestRequest request) {
        log.info("Запуск конкурентного утверждения документов");
        log.info("Документ ID: {}, потоки: {}, попытки: {}",
                request.getDocumentId(), request.getThreads(), request.getAttempts());

        Document document = documentRepository.findByIdWithHistory(request.getDocumentId())
                .orElseThrow(() -> {
                    log.error("Документ с ID {} не найден", request.getDocumentId());
                    return new ResourceNotFoundException("Документ с указанным ID не найден");
                });

        if (document.getStatus() != DocumentStatus.SUBMITTED) {
            log.error("Документ в статусе {}, требуется SUBMITTED", document.getStatus());
            throw new IllegalStateException(
                    String.format("Документ должен быть в статусе SUBMITTED, текущий статус: %s",
                            document.getStatus()));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(request.getThreads());
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(request.getAttempts());

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger conflict = new AtomicInteger(0);
        AtomicInteger registryError = new AtomicInteger(0);
        AtomicInteger notFound = new AtomicInteger(0);

        for (int i = 0; i < request.getAttempts(); i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();

                    ApproveRequestDto approveRequest = ApproveRequestDto.builder()
                            .ids(List.of(request.getDocumentId()))
                            .approver(request.getApprover())
                            .comment("Попытка обработки документа в конкурентном доступе")
                            .build();

                    ApproveResponseDto result = documentService.approveDocuments(approveRequest).getFirst();

                    switch (result.getStatus()) {
                        case SUCCESS -> success.incrementAndGet();
                        case CONFLICT -> conflict.incrementAndGet();
                        case REGISTRY_ERROR -> registryError.incrementAndGet();
                        case NOT_FOUND -> notFound.incrementAndGet();
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    conflict.incrementAndGet();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        log.info("Все потоки запущены, ожидают сигнала старта...");
        startLatch.countDown();
        log.info("Все потоки начали работу");

        try {
            if (!finishLatch.await(30, TimeUnit.SECONDS)) {
                log.warn("Максимальное время выполонения теста (30 секунд) истекло");
            }
        } catch (InterruptedException e) {
            log.error("Тест был прерван");
            Thread.currentThread().interrupt();
        }

        executorService.shutdown();

        Document resDocument = documentRepository.findById(request.getDocumentId()).orElse(null);
        String resStatus = resDocument != null ? resDocument.getStatus().name() : "NOT_FOUND";

        boolean registryCreated = registryRepository.existsByDocumentId(request.getDocumentId());

        log.info("Итоги конкурентного утверждения: всего попыток={}, успешно={}, конфликтов={}, " +
                        "ошибок реестра={}, не найдено={}, финальный статус={}, запись в реестре={}",
                request.getAttempts(), success.get(), conflict.get(), registryError.get(),
                notFound.get(), resStatus, registryCreated);

        return ConcurrencyTestResponse.builder()
                .totalAttempts(request.getAttempts())
                .successful(success.get())
                .conflicts(conflict.get())
                .registryErrors(registryError.get())
                .notFound(notFound.get())
                .finalStatus(resStatus)
                .registryEntryCreated(registryCreated)
                .build();
    }
}
