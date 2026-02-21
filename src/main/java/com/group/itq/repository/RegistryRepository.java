package com.group.itq.repository;

import com.group.itq.model.Registry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistryRepository extends JpaRepository<Registry, Long> {

    @Query("SELECT COUNT(r) > 0 " +
            "FROM Registry r " +
            "WHERE r.document.id = :documentId")
    boolean existsByDocumentId(long documentId);

    @Modifying
    @NativeQuery(value = """
        INSERT INTO registries (document_id, approver) 
        VALUES (:documentId, :approver)
        ON CONFLICT (document_id) DO NOTHING
    """)
    int insertIfNotExists(Long documentId,String approver);
}
