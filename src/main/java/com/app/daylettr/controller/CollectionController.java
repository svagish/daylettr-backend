package com.app.daylettr.controller;

import com.app.daylettr.DTO.CollectedNoteResponse;
import com.app.daylettr.DTO.CollectionResponse;
import com.app.daylettr.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/collection")
public class CollectionController {
    @Autowired
    private CollectionService collectionService;

    @GetMapping
    public ResponseEntity<CollectionResponse> getCollection(@RequestHeader("X-Visitor-Id") UUID visitorId) {
        return ResponseEntity.ok(collectionService.getCollection(visitorId));
    }

    @PostMapping("/{messageId}")
    public ResponseEntity<CollectedNoteResponse> collectNote(@RequestHeader("X-Visitor-Id") UUID visitorId,
                                                             @PathVariable UUID messageId) {
        return ResponseEntity.ok(collectionService.collectNote(visitorId, messageId));
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> removeNote(@RequestHeader("X-Visitor-Id") UUID visitorId,
                                           @PathVariable UUID messageId) {
        collectionService.removeNote(visitorId, messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/has/{messageId}")
    public ResponseEntity<Map<String, Boolean>> isCollected(@RequestHeader("X-Visitor-Id") UUID visitorId,
                                                            @PathVariable UUID messageId) {
        boolean collected = collectionService.isCollected(visitorId, messageId);
        return ResponseEntity.ok(Map.of("collected", collected));
    }
}

