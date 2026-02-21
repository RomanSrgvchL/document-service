package com.group.itq.repository;

import com.group.itq.model.Document;
import com.group.itq.model.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("From Document d LEFT JOIN FETCH d.history WHERE d.id = :documentId")
    Optional<Document> findByIdWithHistory(long documentId);

    @Query("SELECT d FROM Document d WHERE " +
            "(:status IS NULL OR d.status = :status) AND " +
            "(:author IS NULL OR d.author = :author) AND " +
            "(:dateFrom IS NULL OR d.createdAt >= :dateFrom) AND " +
            "(:dateTo IS NULL OR d.createdAt <= :dateTo)")
    Page<Document> search(DocumentStatus status, String author, ZonedDateTime dateFrom,
                          ZonedDateTime dateTo, Pageable pageable);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.status = :status")
    long countByStatus(DocumentStatus status);

    @Query("SELECT d.id FROM Document d WHERE d.status = :status ORDER BY d.id")
    List<Long> findIdsByStatus(DocumentStatus status, Pageable pageable);
}