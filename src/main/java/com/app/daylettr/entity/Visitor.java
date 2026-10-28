package com.app.daylettr.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "visitor")
public class Visitor {
    @Column(name = "id")
    @Id
    private UUID id;

    @Column(name = "last_posted_at")
    private LocalDateTime lastPostedAt;

    @Column(name = "current_streak")
    private int currentStreak;

    @Column(name = "longest_streak")
    private int longestStreak;

    @Column(name = "last_streak_date")
    private LocalDate lastStreakDate;

    public Visitor(UUID visitorId) {
        this.id = visitorId;
        this.currentStreak = 0;
        this.longestStreak = 0;
    }
}
