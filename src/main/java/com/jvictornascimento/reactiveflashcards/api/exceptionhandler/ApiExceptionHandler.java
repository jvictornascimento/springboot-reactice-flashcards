package com.jvictornascimento.reactiveflashcards.api.exceptionhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvictornascimento.reactiveflashcards.api.controller.response.ProblemResponse;
import com.jvictornascimento.reactiveflashcards.domain.exception.ReactiveFlashcardsException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import static com.jvictornascimento.reactiveflashcards.domain.exception.BaseErrorMessage.GENERIC_EXCEPTION;
import static com.jvictornascimento.reactiveflashcards.domain.exception.BaseErrorMessage.GENERIC_METHOD_NOT_ALLOW;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@Order(-2)
@Slf4j
@AllArgsConstructor
public class ApiExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(final ServerWebExchange exchange, final Throwable ex) {
        return Mono.error(ex)
                .onErrorResume(MethodNotAllowedException.class, e ->   handlerMethodNotAllowedException(exchange, e))
                .onErrorResume(ReactiveFlashcardsException.class, e-> handlerReactiveFlashcardException(exchange, e) )
                .onErrorResume(Exception.class, e -> handlerException(exchange, e))
                .onErrorResume(JsonProcessingException.class, e -> handlerJsonProcessingException(exchange, e))
                .then();
    }

    private Mono<Void> handlerException(final ServerWebExchange exchange, final Exception ex) {
        return Mono.fromCallable(() -> {
            prepareExchange(exchange, INTERNAL_SERVER_ERROR);
            return GENERIC_EXCEPTION.getMessage();
        }).map(message -> buildError(INTERNAL_SERVER_ERROR, message))
                .doFirst(() -> log.error("==== Exception ", ex))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }
    private Mono<Void> handlerReactiveFlashcardException(final ServerWebExchange exchange, final ReactiveFlashcardsException ex) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, INTERNAL_SERVER_ERROR);
                    return GENERIC_EXCEPTION.getMessage();
                }).map(message -> buildError(INTERNAL_SERVER_ERROR, message))
                .doFirst(() -> log.error("==== ReactiveFlashcardException ", ex))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }
    private Mono<Void> handlerMethodNotAllowedException(final ServerWebExchange exchange, final MethodNotAllowedException ex) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, METHOD_NOT_ALLOWED);
                    return GENERIC_METHOD_NOT_ALLOW.getMessage();
                }).map(message -> buildError(METHOD_NOT_ALLOWED, message))
                .doFirst(() -> log.error("==== MethodNotAllowedException: Method[{}] is not allowed at [{}]",
                        exchange.getRequest().getMethod(), exchange.getRequest().getPath().value(), ex))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }
    private Mono<Void> handlerJsonProcessingException(final ServerWebExchange exchange, final JsonProcessingException ex) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, METHOD_NOT_ALLOWED);
                    return GENERIC_METHOD_NOT_ALLOW.getMessage();
                }).map(message -> buildError(METHOD_NOT_ALLOWED, message))
                .doFirst(() -> log.error("==== JsonProcessingException: Failed to map exception for the request {} "
                        ,exchange.getRequest().getMethod(), ex))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> writeResponse(ServerWebExchange exchange, ProblemResponse problemResponse) {
        return exchange.getResponse()
                .writeWith(Mono.fromCallable(() ->
                        new DefaultDataBufferFactory()
                                .wrap(objectMapper.writeValueAsBytes(problemResponse))));
    }

    private void prepareExchange(final ServerWebExchange exchange, final HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(APPLICATION_JSON);
    }
    private ProblemResponse buildError(final HttpStatus status, final String erroDescription) {
        return ProblemResponse.builder()
                .status(status.value())
                .errorDescription(erroDescription )
                .build();
    }
}
