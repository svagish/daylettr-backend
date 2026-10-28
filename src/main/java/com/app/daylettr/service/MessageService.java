package com.app.daylettr.service;

import com.app.daylettr.DTO.CreateMessageRequest;
import com.app.daylettr.DTO.DailyActivityResponse;
import com.app.daylettr.DTO.VisitorStreakResponse;
import com.app.daylettr.DTO.YesterdayMessagesResponse;
import com.app.daylettr.entity.DailyVisitorStat;
import com.app.daylettr.entity.Message;
import com.app.daylettr.entity.Visitor;
import com.app.daylettr.exception.DailyLimitExceededException;
import com.app.daylettr.exception.ResourceNotFoundException;
import com.app.daylettr.repository.DailyVisitorStatRepository;
import com.app.daylettr.repository.MessageRepository;
import com.app.daylettr.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private VisitorRepository visitorRepository;
    @Autowired
    private DailyVisitorStatRepository dailyVisitorStatRepository;

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
        msg.setVisitorId(visitorId);
        msg.setContent(createMessageRequest.getContent());

        // the 6-hour cooldown starts from the moment they FINISHED typing.
        vis.setLastPostedAt(LocalDateTime.now());
        recordDay(vis);
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

    /**
     * Updates today's DailyVisitorStat (post count + last post time) and the
     * streak counters on the Visitor. Called inside createMessage's transaction,
     * so the visitor save below persists both in one round trip.
     */
    private void recordDay(Visitor vis) {
        LocalDate today = LocalDate.now();

        DailyVisitorStat stat = dailyVisitorStatRepository.findByVisitorIdAndStatDate(vis.getId(), today)
                .orElseGet(() -> {
                    DailyVisitorStat s = new DailyVisitorStat();
                    s.setVisitorId(vis.getId());
                    s.setStatDate(today);
                    return s;
                });
        stat.setMessagesPosted(stat.getMessagesPosted() + 1);
        stat.setLastPostedAt(LocalDateTime.now());
        dailyVisitorStatRepository.save(stat);

        // streak rules: already-counted-today is a no-op, yesterday continues, anything else resets
        LocalDate last = vis.getLastStreakDate();
        if (last == null) {
            vis.setCurrentStreak(1);
        } else if (last.equals(today)) {
            // already counted today — do not bump again
        } else if (last.equals(today.minusDays(1))) {
            vis.setCurrentStreak(vis.getCurrentStreak() + 1);
        } else {
            vis.setCurrentStreak(1);
        }
        if (vis.getCurrentStreak() > vis.getLongestStreak()) {
            vis.setLongestStreak(vis.getCurrentStreak());
        }
        vis.setLastStreakDate(today);
    }

    @Transactional(readOnly = true)
    public VisitorStreakResponse getStreak(UUID visitorId) {
        Visitor vis = visitorRepository.findById(visitorId).orElse(null);
        if (vis == null) {
            return VisitorStreakResponse.ofNewVisitor(LocalDate.now());
        }

        LocalDate today = LocalDate.now();

        List<DailyVisitorStat> window = dailyVisitorStatRepository
                .findByVisitorIdAndStatDateBetweenOrderByStatDateAsc(visitorId, today.minusDays(6), today);
        Map<LocalDate, DailyVisitorStat> byDate = new java.util.HashMap<>();
        for (DailyVisitorStat s : window) {
            byDate.put(s.getStatDate(), s);
        }

        List<DailyActivityResponse> recentDays = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            DailyVisitorStat s = byDate.get(d);
            recentDays.add(new DailyActivityResponse(d, s != null, s == null ? 0 : s.getMessagesPosted()));
        }

        boolean postedToday = byDate.containsKey(today);
        LocalDate lastPostedDate = null;
        for (int i = 0; i < 7; i++) {
            if (byDate.containsKey(today.minusDays(i))) {
                lastPostedDate = today.minusDays(i);
                break;
            }
        }
        // fall back to the visitor's own record if the last post is older than 7 days
        if (lastPostedDate == null && vis.getLastPostedAt() != null) {
            lastPostedDate = vis.getLastPostedAt().toLocalDate();
        }

        long totalDaysPosted = dailyVisitorStatRepository.countByVisitorId(visitorId);
        Long sum = dailyVisitorStatRepository.sumMessagesPostedByVisitorId(visitorId);
        long totalMessagesPosted = sum == null ? 0 : sum;

        return new VisitorStreakResponse(
                vis.getCurrentStreak(),
                vis.getLongestStreak(),
                postedToday,
                lastPostedDate,
                totalDaysPosted,
                totalMessagesPosted,
                recentDays);
    }
}
