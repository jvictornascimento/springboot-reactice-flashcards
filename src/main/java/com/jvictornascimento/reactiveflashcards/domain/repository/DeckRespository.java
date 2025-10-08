package com.jvictornascimento.reactiveflashcards.domain.repository;

import com.jvictornascimento.reactiveflashcards.domain.document.DeckDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeckRespository extends ReactiveMongoRepository<DeckDocument, String> {
}
