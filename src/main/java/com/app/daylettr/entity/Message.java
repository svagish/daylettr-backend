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
@Table(name = "message")
public class Message {
    @GeneratedValue
    @Column(name = "id")
    @Id
    private UUID id;

    @Column(name = "visitor_id")
    private UUID visitorId;

    @Column(name = "content")
    private String content;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "rating_sum")
    private int ratingSum;

    @Column(name = "rating_count")
    private int ratingCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
