package com.app.daylettr.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "collected_note",
        uniqueConstraints = @UniqueConstraint(columnNames = {"visitor_id", "message_id"}))
public class CollectedNote {
    @GeneratedValue
    @Column(name = "id")
    @Id
    private UUID id;

    @Column(name = "visitor_id")
    private UUID visitorId;

    @Column(name = "message_id")
    private UUID messageId;

    @Column(name = "collected_at")
    private LocalDateTime collectedAt;

    @PrePersist
    public void onCreate() {
        this.collectedAt = LocalDateTime.now();
    }
}
