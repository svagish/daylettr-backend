package com.app.daylettr.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public Visitor(UUID visitorId) {
        this.id = visitorId;
    }
}
