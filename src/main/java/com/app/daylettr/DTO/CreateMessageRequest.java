package com.app.daylettr.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateMessageRequest {
    @NotBlank(message = "Content must not be blank")
    @Size(max = 500, message = "Content must be less than 500 characters")
    private String content;
    private UUID previousNoteId; // The ID of the note the user just read
    private int rating;
}
