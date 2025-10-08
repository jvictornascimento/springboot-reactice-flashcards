package com.jvictornascimento.reactiveflashcards.api.controller;

import com.jvictornascimento.reactiveflashcards.api.controller.request.UserRequest;
import com.jvictornascimento.reactiveflashcards.api.controller.response.UserResponse;
import com.jvictornascimento.reactiveflashcards.api.mapper.UserMapper;
import com.jvictornascimento.reactiveflashcards.domain.service.UserService;
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
@RequestMapping("/users")
@Slf4j
@AllArgsConstructor
public class UserController {
    private final UserService service;
    private final UserMapper mapper;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Mono<UserResponse> save(@Valid @RequestBody final UserRequest request) {
        return service.save(mapper.toDocumento(request))
                .doFirst(() -> log.info("==== Saving a user with follow data {}", request))
                .map(mapper::toResponse);
    }
}
