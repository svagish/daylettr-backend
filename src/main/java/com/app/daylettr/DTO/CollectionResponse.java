package com.app.daylettr.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CollectionResponse {
    private int totalCollected;
    private List<CollectedNoteResponse> notes;
}
