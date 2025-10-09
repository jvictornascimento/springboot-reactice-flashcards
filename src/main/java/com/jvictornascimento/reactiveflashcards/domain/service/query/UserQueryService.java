package com.jvictornascimento.reactiveflashcards.domain.service.query;

import com.jvictornascimento.reactiveflashcards.domain.document.UserDocument;
import com.jvictornascimento.reactiveflashcards.domain.exception.NotFoundException;
import com.jvictornascimento.reactiveflashcards.domain.repository.UserRespository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.jvictornascimento.reactiveflashcards.domain.exception.BaseErrorMessage.USER_NOT_FOUND;

@Service
@Slf4j
@AllArgsConstructor
public class UserQueryService {
    private final UserRespository respository;
    public Mono<UserDocument> findById(final String id) {
        return respository.findById(id)
                .doFirst(() -> log.info("==== try to find user with id {}", id))
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.defer(()-> Mono.error(new NotFoundException(USER_NOT_FOUND.params(id).getMessage()))));
    }
}
