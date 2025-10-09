package com.jvictornascimento.reactiveflashcards.api.exceptionhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvictornascimento.reactiveflashcards.api.controller.response.ErrorFieldsResponse;
import com.jvictornascimento.reactiveflashcards.api.controller.response.ProblemResponse;
import com.jvictornascimento.reactiveflashcards.domain.exception.NotFoundException;
import com.jvictornascimento.reactiveflashcards.domain.exception.ReactiveFlashcardsException;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.jvictornascimento.reactiveflashcards.domain.exception.BaseErrorMessage.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@Order(-2)
@Slf4j
@AllArgsConstructor
public class ApiExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;


    @Override
    public Mono<Void> handle(final ServerWebExchange exchange, final Throwable ex) {
        return Mono.error(ex)
                .onErrorResume(MethodNotAllowedException.class, e ->   handlerMethodNotAllowedException(exchange, e))
                .onErrorResume(NotFoundException.class,e -> handlerNotFoundException(exchange, e))
                .onErrorResume(ConstraintViolationException.class, e -> handlerConstraintViolationException(exchange, e))
                .onErrorResume(WebExchangeBindException.class, e -> handlerWebExchangeBindException(exchange, e))
                .onErrorResume(ResponseStatusException.class,e -> handlerResponseStatusException(exchange,e))
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
    private Mono<Void> handlerConstraintViolationException(final ServerWebExchange exchange, final ConstraintViolationException ex) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, BAD_REQUEST);
                    return GENERIC_BAD_REQUEST.getMessage();
                }).map(message -> buildError(BAD_REQUEST, message))
                .flatMap(response -> buildParamErrorMessage(response, ex))
                .doFirst(() -> log.error("==== ConstraintViolationException", ex))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }
    private Mono<ProblemResponse> buildParamErrorMessage(final ProblemResponse response, final ConstraintViolationException ex) {
        return Flux.fromIterable(ex.getConstraintViolations())
                .map(constraintViolation -> ErrorFieldsResponse.builder()
                                        .name(((PathImpl) constraintViolation.getPropertyPath()).getLeafNode().toString())
                                        .message(constraintViolation.getMessage()).build())
                .collectList()
                .map(problems -> response.toBuilder().fields(problems).build());
    }
    private Mono<Void> handlerResponseStatusException(final ServerWebExchange exchange, final ResponseStatusException ex) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, BAD_REQUEST);
                    return GENERIC_BAD_REQUEST.getMessage();
                }).map(message -> buildError(BAD_REQUEST, message))
                .doFirst(() -> log.error("==== ResponseStatusException", ex))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }
    private Mono<Void> handlerWebExchangeBindException(final ServerWebExchange exchange, final WebExchangeBindException ex) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, BAD_REQUEST);
                    return GENERIC_BAD_REQUEST.getMessage();
                }).map(message -> buildError(BAD_REQUEST, message))
                .flatMap(response -> buildParamErrorMessage(response, ex))
                .doFirst(() -> log.error("==== WebExchangeBindException", ex))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }
    private Mono<ProblemResponse> buildParamErrorMessage(final ProblemResponse response, final WebExchangeBindException ex) {
        return Flux.fromIterable(ex.getAllErrors())
                .map(objectError -> ErrorFieldsResponse.builder()
                        .name(objectError instanceof FieldError fieldError? fieldError.getField() : objectError.getObjectName())
                        .message(messageSource.getMessage(objectError, LocaleContextHolder.getLocale()))
                        .build())
                .collectList()
                .map(problems -> response.toBuilder().fields(problems).build());
    }

    private Mono<Void> handlerNotFoundException(final ServerWebExchange exchange, final NotFoundException ex) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, NOT_FOUND);
                    return ex.getMessage();
                }).map(message -> buildError(NOT_FOUND, message))
                .doFirst(() -> log.error("==== NotFoundException", ex))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }
    private Mono<Void> handlerMethodNotAllowedException(final ServerWebExchange exchange, final MethodNotAllowedException ex) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, METHOD_NOT_ALLOWED);
                    return GENERIC_METHOD_NOT_ALLOW.params(exchange.getRequest().getMethod().name()).getMessage();
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
                        ,exchange.getRequest().getPath().value(), ex))
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
