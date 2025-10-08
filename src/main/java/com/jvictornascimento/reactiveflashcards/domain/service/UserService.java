package com.jvictornascimento.reactiveflashcards.domain.service;

import com.jvictornascimento.reactiveflashcards.domain.document.UserDocument;
import com.jvictornascimento.reactiveflashcards.domain.repository.UserRespository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
@Service
public class UserService {
    private final UserRespository userRespository;

    public Mono<UserDocument> save(final UserDocument document) {
        return userRespository.save(document)
                .doFirst(() -> log.info("==== try to save a follow document {}", document));
    }
}
