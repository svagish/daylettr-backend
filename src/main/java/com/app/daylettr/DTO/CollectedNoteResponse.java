package com.app.daylettr.DTO;

import com.app.daylettr.entity.CollectedNote;
import com.app.daylettr.entity.Message;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CollectedNoteResponse {
    private UUID messageId;
    private String content;
    private LocalDateTime noteCreatedAt;
    private LocalDateTime collectedAt;
    private int ratingSum;
    private int ratingCount;

    public CollectedNoteResponse() {
    }

    public CollectedNoteResponse(UUID messageId, String content, LocalDateTime noteCreatedAt,
                                 LocalDateTime collectedAt, int ratingSum, int ratingCount) {
        this.messageId = messageId;
        this.content = content;
        this.noteCreatedAt = noteCreatedAt;
        this.collectedAt = collectedAt;
        this.ratingSum = ratingSum;
        this.ratingCount = ratingCount;
    }

    public static CollectedNoteResponse of(Message msg, CollectedNote col) {
        return new CollectedNoteResponse(
                msg.getId(),
                msg.getContent(),
                msg.getCreatedAt(),
                col.getCollectedAt(),
                msg.getRatingSum(),
                msg.getRatingCount()
        );
    }
}
