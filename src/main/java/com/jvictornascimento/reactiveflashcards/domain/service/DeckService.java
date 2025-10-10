package com.jvictornascimento.reactiveflashcards.domain.service;

import com.jvictornascimento.reactiveflashcards.domain.document.DeckDocument;
import com.jvictornascimento.reactiveflashcards.domain.repository.DeckRespository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@AllArgsConstructor
public class DeckService {
    private final DeckRespository deckRespository;

    public Mono<DeckDocument> save(final DeckDocument document) {
        return deckRespository.save(document)
                .doFirst(() -> log.info("==== Try to save a follow deck {}", document));
    }
}
