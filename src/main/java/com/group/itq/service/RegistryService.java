package com.group.itq.service;

import com.group.itq.model.Document;
import com.group.itq.repository.RegistryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistryService {

    private final RegistryRepository registryRepository;

    @Transactional
    public boolean registerApproval(Document document, String approver) {
        log.info("Регистрация документа ID={} в реестре, утверждающий={}", document.getId(), approver);

        try {
            int inserted = registryRepository.insertIfNotExists(document.getId(), approver);
            if (inserted > 0) {
                log.info("Документ зарегистрирован в реестре id={}", document.getId());
                return true;
            } else {
                log.warn("Документ уже зарегистрирован id={}", document.getId());
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка регистрации документа в реестре id={}", document.getId(), e);
            return false;
        }
    }
}
