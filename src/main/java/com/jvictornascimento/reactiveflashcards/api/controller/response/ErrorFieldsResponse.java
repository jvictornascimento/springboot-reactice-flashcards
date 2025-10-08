package com.jvictornascimento.reactiveflashcards.api.controller.response;

import lombok.Builder;

public record ErrorFieldsResponse(String name,
                                  String message) {
    @Builder(toBuilder = true)
    public ErrorFieldsResponse{

    }
}
