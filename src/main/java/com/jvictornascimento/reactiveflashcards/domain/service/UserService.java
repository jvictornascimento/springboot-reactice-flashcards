package com.jvictornascimento.reactiveflashcards.domain.service;

import com.jvictornascimento.reactiveflashcards.domain.document.UserDocument;
import com.jvictornascimento.reactiveflashcards.domain.repository.UserRespository;
import com.jvictornascimento.reactiveflashcards.domain.service.query.UserQueryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
@Service
public class UserService {
    private final UserRespository userRespository;
    private final UserQueryService userQueryService;

    public Mono<UserDocument> save(final UserDocument document) {
        return userRespository.save(document)
                .doFirst(() -> log.info("==== Try to save a follow document {}", document));
    }
    public Mono<UserDocument> update(final UserDocument document) {
        return userQueryService.findById(document.id())
                .map(user-> document.toBuilder()
                        .createAt(user.createAt())
                        .updateAt(user.updateAt())
                        .build())
                .flatMap(userRespository::save)
                .doFirst(()-> log.info("==== Try to update a user with follow info {}",document));
    }
}
