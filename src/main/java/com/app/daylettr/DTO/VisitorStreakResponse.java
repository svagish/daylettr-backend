package com.app.daylettr.DTO;

import com.app.daylettr.entity.Visitor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class VisitorStreakResponse {
    private int currentStreak;
    private int longestStreak;
    private boolean postedToday;
    private LocalDate lastPostedDate;
    private long totalDaysPosted;
    private long totalMessagesPosted;
    private List<DailyActivityResponse> recentDays;

    public VisitorStreakResponse() {
    }

    public VisitorStreakResponse(int currentStreak, int longestStreak, boolean postedToday,
                                 LocalDate lastPostedDate, long totalDaysPosted,
                                 long totalMessagesPosted, List<DailyActivityResponse> recentDays) {
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.postedToday = postedToday;
        this.lastPostedDate = lastPostedDate;
        this.totalDaysPosted = totalDaysPosted;
        this.totalMessagesPosted = totalMessagesPosted;
        this.recentDays = recentDays;
    }

    /**
     * Zero-state for unknown visitors (never 404).
     */
    public static VisitorStreakResponse ofNewVisitor(LocalDate today) {
        return new VisitorStreakResponse(0, 0, false, null, 0, 0,
                emptyRecentDays(today));
    }

    public static List<DailyActivityResponse> emptyRecentDays(LocalDate today) {
        java.util.ArrayList<DailyActivityResponse> days = new java.util.ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            days.add(new DailyActivityResponse(today.minusDays(i), false, 0));
        }
        return days;
    }
}
