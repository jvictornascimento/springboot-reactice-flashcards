package com.jvictornascimento.reactiveflashcards.api.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record CardRequest(@JsonProperty("front")
                          @NotBlank
                          @Size(min = 1, max = 255)
                          String front,
                          @NotBlank
                          @Size(min = 1, max = 255)
                          @JsonProperty("back")
                          String back) {
    @Builder(toBuilder = true)
    public CardRequest{}
}
