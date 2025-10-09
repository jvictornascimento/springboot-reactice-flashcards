package com.jvictornascimento.reactiveflashcards.api.exceptionhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvictornascimento.reactiveflashcards.api.controller.response.ErrorFieldsResponse;
import com.jvictornascimento.reactiveflashcards.api.controller.response.ProblemResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.jvictornascimento.reactiveflashcards.domain.exception.BaseErrorMessage.GENERIC_BAD_REQUEST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
@Slf4j
@Component
public class ConstraintViolationHandler extends AbstractHandlerException<ConstraintViolationException>{
    public ConstraintViolationHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    Mono<Void> handlerException(ServerWebExchange exchange, ConstraintViolationException ex) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, BAD_REQUEST);
                    return GENERIC_BAD_REQUEST.getMessage();
                }).map(message -> buildError(BAD_REQUEST, message))
                .flatMap(response -> buildRequestErrorMessage(response, ex))
                .doFirst(() -> log.error("==== ConstraintViolationException", ex))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }
    private Mono<ProblemResponse> buildRequestErrorMessage(final ProblemResponse response, final ConstraintViolationException ex) {
        return Flux.fromIterable(ex.getConstraintViolations())
                .map(constraintViolation -> ErrorFieldsResponse.builder()
                        .name(((PathImpl) constraintViolation.getPropertyPath()).getLeafNode().toString())
                        .message(constraintViolation.getMessage()).build())
                .collectList()
                .map(problems -> response.toBuilder().fields(problems).build());
    }
}
