package com.app.daylettr.service;

import com.app.daylettr.DTO.CollectedNoteResponse;
import com.app.daylettr.DTO.CollectionResponse;
import com.app.daylettr.entity.CollectedNote;
import com.app.daylettr.entity.Message;
import com.app.daylettr.exception.ResourceNotFoundException;
import com.app.daylettr.repository.CollectedNoteRepository;
import com.app.daylettr.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CollectionService {
    @Autowired
    private CollectedNoteRepository collectedNoteRepository;
    @Autowired
    private MessageRepository messageRepository;

    @Transactional
    public CollectedNoteResponse collectNote(UUID visitorId, UUID messageId) {
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));

        // Idempotent: return the existing collection entry if already collected
        CollectedNote existing = collectedNoteRepository.findByVisitorIdAndMessageId(visitorId, messageId)
                .orElseGet(() -> {
                    CollectedNote note = new CollectedNote();
                    note.setVisitorId(visitorId);
                    note.setMessageId(messageId);
                    return note;
                });
        return CollectedNoteResponse.of(msg, collectedNoteRepository.save(existing));
    }

    @Transactional
    public void removeNote(UUID visitorId, UUID messageId) {
        CollectedNote note = collectedNoteRepository.findByVisitorIdAndMessageId(visitorId, messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Note is not in your collection"));
        collectedNoteRepository.delete(note);
    }

    @Transactional(readOnly = true)
    public CollectionResponse getCollection(UUID visitorId) {
        List<CollectedNote> collectedNotes = collectedNoteRepository.findByVisitorIdOrderByCollectedAtDesc(visitorId);
        List<CollectedNoteResponse> notes = collectedNotes.stream()
                .map(col -> {
                    Message msg = messageRepository.findById(col.getMessageId()).orElse(null);
                    return msg != null ? CollectedNoteResponse.of(msg, col) : null;
                })
                .filter(n -> n != null)
                .toList();
        return new CollectionResponse(notes.size(), notes);
    }

    @Transactional(readOnly = true)
    public boolean isCollected(UUID visitorId, UUID messageId) {
        return collectedNoteRepository.existsByVisitorIdAndMessageId(visitorId, messageId);
    }
}
