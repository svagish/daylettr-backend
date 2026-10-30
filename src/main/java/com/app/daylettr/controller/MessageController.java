package com.app.daylettr.controller;

import com.app.daylettr.DTO.CreateMessageRequest;
import com.app.daylettr.DTO.VisitorStreakResponse;
import com.app.daylettr.DTO.WordsTodayResponse;
import com.app.daylettr.DTO.YesterdayMessagesResponse;
import com.app.daylettr.entity.Message;
import com.app.daylettr.service.MessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
@Validated
@RestController
@RequestMapping("/api")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("/message")
    public ResponseEntity<Message> getMessage(@NotNull @RequestHeader("X-Visitor-Id") UUID visitorId) {
        Message msg = messageService.getMessage(visitorId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(msg);
    }

    @PostMapping("/message")
    public ResponseEntity<Message> createMessage(@NotNull @RequestHeader("X-Visitor-Id") UUID visitorId,
                                                 @Valid @RequestBody CreateMessageRequest createMessageRequest) {
        Message savedMsg = messageService.createMessage(visitorId, createMessageRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(savedMsg);
    }

    @GetMapping("/message/yesterday")
    public ResponseEntity<YesterdayMessagesResponse> getYesterdaysMessages() {
        YesterdayMessagesResponse yesterdayMessagesResponse = messageService.getYesterdaysMessages();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(yesterdayMessagesResponse);
    }

    @GetMapping("visitor/streak")
    public ResponseEntity<VisitorStreakResponse> getStreak(@RequestHeader("X-Visitor-Id") UUID visitorId) {
        return ResponseEntity.ok(messageService.getStreak(visitorId));
    }

    // Community-wide word count for today; public, like /message/yesterday.
    // Frontend re-fetches after POST /message so the count includes the user's own words.
    @GetMapping("/message/words-today")
    public ResponseEntity<WordsTodayResponse> getWordsToday() {
        return ResponseEntity.ok(new WordsTodayResponse(
                messageService.getWordsSharedToday()));
    }

    @GetMapping("visitor/status")
    public ResponseEntity<Map<String, Object>> checkStatus(@RequestHeader("X-Visitor-Id") UUID visitorId) {
        boolean onCooldown = messageService.isUserOnCooldown(visitorId);
        LocalDateTime lastPostedAt = messageService.getLastPostedAt(visitorId);
        return ResponseEntity.ok(Map.of(
                "onCooldown", onCooldown,
                "lastPostedAt", lastPostedAt
        ));
    }

}
