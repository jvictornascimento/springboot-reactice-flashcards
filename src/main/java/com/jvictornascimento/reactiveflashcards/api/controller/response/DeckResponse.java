package com.jvictornascimento.reactiveflashcards.api.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jvictornascimento.reactiveflashcards.api.controller.request.CardRequest;
import lombok.Builder;

import java.util.Set;

public record DeckResponse(@JsonProperty("id")
                           String id,
                           @JsonProperty("name")
                           String name,
                           @JsonProperty("description")
                           String description,
                           @JsonProperty("cards")
                           Set<CardRequest> cards) {
@Builder(toBuilder = true)
    public DeckResponse {}
}
