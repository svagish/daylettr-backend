package com.app.daylettr.DTO;

import com.app.daylettr.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class YesterdayMessagesResponse {
    private int totalMessages;
    private List<Message> messages;
}
