package com.jvictornascimento.reactiveflashcards.api.controller;

import com.jvictornascimento.reactiveflashcards.api.controller.request.DeckRequest;
import com.jvictornascimento.reactiveflashcards.api.controller.response.DeckResponse;
import com.jvictornascimento.reactiveflashcards.api.mapper.DeckMapper;
import com.jvictornascimento.reactiveflashcards.core.validation.MongoId;
import com.jvictornascimento.reactiveflashcards.domain.service.DeckService;
import com.jvictornascimento.reactiveflashcards.domain.service.query.DeckQueryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Validated
@RestController
@RequestMapping("/decks")
@Slf4j
@AllArgsConstructor
public class DeckController {
    private final DeckService deckService;
    private final DeckMapper deckMapper;
    private final DeckQueryService deckQueryService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Mono<DeckResponse> save(@Valid @RequestBody final DeckRequest request) {
        return deckService.save(deckMapper.toDocumento(request))
                .doFirst(() -> log.info("==== Saving a user with follow data {}", request))
                .map(deckMapper::toResponse);
    }
    @GetMapping(produces = APPLICATION_JSON_VALUE, value = "{id}")
    public Mono<DeckResponse> findById (@Valid @PathVariable @MongoId(message = "{deckController.id}") final String id) {
        return deckQueryService.findById(id)
                .doFirst(() -> log.info("==== Finding a deck with follow id {}", id))
                .map(deckMapper::toResponse);
    }
}
