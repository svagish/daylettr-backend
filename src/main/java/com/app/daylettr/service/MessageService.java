package com.app.daylettr.service;

import com.app.daylettr.DTO.CreateMessageRequest;
import com.app.daylettr.DTO.YesterdayMessagesResponse;
import com.app.daylettr.entity.Message;
import com.app.daylettr.entity.Visitor;
import com.app.daylettr.exception.DailyLimitExceededException;
import com.app.daylettr.exception.ResourceNotFoundException;
import com.app.daylettr.repository.MessageRepository;
import com.app.daylettr.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private VisitorRepository visitorRepository;

    @Transactional
    public Message createMessage(UUID visitorId, CreateMessageRequest createMessageRequest) {
        Visitor vis = visitorRepository.findById(visitorId)
                .orElseGet(() -> new Visitor(visitorId));
        // Refined constraint: Check if timestamp is more than 15 minutes old
//        if (vis.getLastPostedAt() != null &&
//                vis.getLastPostedAt().isBefore(LocalDateTime.now().minusMinutes(15))) {
//            throw new DailyLimitExceededException("Session expired.");
//        }

        // set rating for previous message
        if (createMessageRequest.getPreviousNoteId() != null) {
            Message previousMsg = messageRepository.findById(createMessageRequest.getPreviousNoteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Previous note not found"));

            previousMsg.setRatingSum(previousMsg.getRatingSum() + createMessageRequest.getRating());
            previousMsg.setRatingCount(previousMsg.getRatingCount() + 1);
            messageRepository.save(previousMsg);
        }

        Message msg = new Message();
        msg.setContent(createMessageRequest.getContent());

        // the 6-hour cooldown starts from the moment they FINISHED typing.
        vis.setLastPostedAt(LocalDateTime.now());
        visitorRepository.save(vis);

        return messageRepository.save(msg);
    }


    @Transactional
    public Message getMessage(UUID visitorId) {
        Visitor vis = visitorRepository.findById(visitorId)
                .orElseGet(()->new Visitor(visitorId));
        // if visitor is not new and posted in last 6 hrs - cooldown
        if (vis.getLastPostedAt() != null && vis.getLastPostedAt().isAfter(LocalDateTime.now().minusHours(6))) {
            throw new DailyLimitExceededException("You must wait 6 hours between notes.");
        }

        // try to fetch message, handle 404 in frontend for get message
        Message msg = messageRepository.findFirstByOrderByCreatedAtDesc()
            .orElseThrow(() -> new ResourceNotFoundException("The queue is empty! Be the first to write."));
        // mark visitor for cooldown
        vis.setLastPostedAt(LocalDateTime.now());
        visitorRepository.save(vis);
        return msg;
    }

    @Transactional
    public YesterdayMessagesResponse getYesterdaysMessages() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(LocalTime.MAX);
        List<Message> yesterdayMessagesList = messageRepository.findByCreatedAtBetweenOrderByCreatedAt(start, end);
        if (yesterdayMessagesList.isEmpty()) {
           throw new ResourceNotFoundException("No messages were made yesterday.");
        }
        return new YesterdayMessagesResponse(yesterdayMessagesList.size(), yesterdayMessagesList);
    }

    // cooldown if posted in last 6 hours
    @Transactional
    public boolean isUserOnCooldown(UUID visitorId) {
        return visitorRepository.findById(visitorId)
                .map(vis -> vis.getLastPostedAt() != null &&
                        vis.getLastPostedAt().isAfter(LocalDateTime.now().minusHours(6)))
                .orElse(false); // New users are never on cooldown
    }

    public LocalDateTime getLastPostedAt(UUID visitorId) {
        return visitorRepository.findById(visitorId)
                .map(Visitor::getLastPostedAt)
                .orElseThrow(()->new ResourceNotFoundException("Visitor not found"));
    }
}
