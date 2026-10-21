package com.app.daylettr.repository;

import com.app.daylettr.entity.CollectedNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollectedNoteRepository extends JpaRepository<CollectedNote, UUID> {
    boolean existsByVisitorIdAndMessageId(UUID visitorId, UUID messageId);

    List<CollectedNote> findByVisitorIdOrderByCollectedAtDesc(UUID visitorId);

    Optional<CollectedNote> findByVisitorIdAndMessageId(UUID visitorId, UUID messageId);

    void deleteByVisitorIdAndMessageId(UUID visitorId, UUID messageId);
}
