package com.jvictornascimento.reactiveflashcards.domain.repository;

import com.jvictornascimento.reactiveflashcards.domain.document.UserDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRespository extends ReactiveMongoRepository<UserDocument, String> {
}
