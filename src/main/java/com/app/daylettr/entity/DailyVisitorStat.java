package com.app.daylettr.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "daily_visitor_stat",
        uniqueConstraints = @UniqueConstraint(columnNames = {"visitor_id", "stat_date"}))
public class DailyVisitorStat {
    @GeneratedValue
    @Column(name = "id")
    @Id
    private UUID id;

    @Column(name = "visitor_id")
    private UUID visitorId;

    @Column(name = "stat_date")
    private LocalDate statDate;

    @Column(name = "messages_posted")
    private int messagesPosted;

    @Column(name = "last_posted_at")
    private LocalDateTime lastPostedAt;
}
