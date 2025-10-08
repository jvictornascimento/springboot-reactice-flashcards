package com.jvictornascimento.reactiveflashcards.domain.repository;

import com.jvictornascimento.reactiveflashcards.domain.document.StudyDeck;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRespository extends ReactiveMongoRepository<StudyDeck, String> {
}
