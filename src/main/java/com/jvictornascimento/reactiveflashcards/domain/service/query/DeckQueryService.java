package com.jvictornascimento.reactiveflashcards.domain.service.query;

import com.jvictornascimento.reactiveflashcards.domain.document.DeckDocument;
import com.jvictornascimento.reactiveflashcards.domain.exception.NotFoundException;
import com.jvictornascimento.reactiveflashcards.domain.repository.DeckRespository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.jvictornascimento.reactiveflashcards.domain.exception.BaseErrorMessage.DECK_NOT_FOUND;

@Service
@Slf4j
@AllArgsConstructor
public class DeckQueryService {
    private final DeckRespository deckRespository;

    public Mono<DeckDocument> findById(final String id) {
        return deckRespository.findById(id)
                .doFirst(() -> log.info("==== try to find deck with id {}", id))
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.defer(()->
                        Mono.error(new NotFoundException(DECK_NOT_FOUND.params(id).getMessage()))));
    }

}
