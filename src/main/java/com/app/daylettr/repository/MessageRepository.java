package com.app.daylettr.repository;

import com.app.daylettr.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    // Finds the oldest message that hasn't been read yet
    Optional<Message> findFirstByIsReadFalseOrderByCreatedAtAsc();
    Optional<Message> findFirstByOrderByCreatedAtDesc();

    // For your "Yesterday's Notes" feature
    List<Message> findByCreatedAtBetweenOrderByCreatedAt(LocalDateTime start, LocalDateTime end);
}
