package com.jvictornascimento.reactiveflashcards.domain.document;

import lombok.Builder;

public record Card(String front,
                   String back) {
    @Builder
    public Card{}
}
