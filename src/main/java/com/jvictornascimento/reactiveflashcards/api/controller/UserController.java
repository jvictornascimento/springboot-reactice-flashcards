package com.jvictornascimento.reactiveflashcards.api.controller;

import com.jvictornascimento.reactiveflashcards.api.controller.request.UserRequest;
import com.jvictornascimento.reactiveflashcards.api.controller.response.UserResponse;
import com.jvictornascimento.reactiveflashcards.api.mapper.UserMapper;
import com.jvictornascimento.reactiveflashcards.core.validation.MongoId;
import com.jvictornascimento.reactiveflashcards.domain.service.UserService;
import com.jvictornascimento.reactiveflashcards.domain.service.query.UserQueryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
    private final UserQueryService userQueryService;
    private final UserMapper mapper;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Mono<UserResponse> save(@Valid @RequestBody final UserRequest request) {
        return service.save(mapper.toDocumento(request))
                .doFirst(() -> log.info("==== Saving a user with follow data {}", request))
                .map(mapper::toResponse);
    }
    @GetMapping(produces = APPLICATION_JSON_VALUE, value = "{id}")
    public Mono<UserResponse> findById(@PathVariable @Valid @MongoId(message = "{userController.id}") final String id ) {
        return userQueryService.findById(id)
                .doFirst(() -> log.info("===== finding a user with follow id {}", id))
                .map(mapper::toResponse);
    }
    @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE, value = "{id}")
    public Mono<UserResponse> update(@PathVariable @Valid @MongoId(message = "{userController.id}") final String id,
                                     @Valid @RequestBody final UserRequest request) {
            return service.update(mapper.toDocumento(request, id))
                    .doFirst(()-> log.info("==== Updating a user with follow info [body: {}, id: {}", request, id))
                    .map(mapper::toResponse);

    }

}
