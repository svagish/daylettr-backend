package com.app.daylettr.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class DailyActivityResponse {
    private LocalDate date;
    private boolean posted;
    private int messagesPosted;
}
