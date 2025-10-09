package com.jvictornascimento.reactiveflashcards.api.exceptionhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvictornascimento.reactiveflashcards.api.controller.response.ProblemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON;
@RequiredArgsConstructor
public abstract class AbstractHandlerException <T extends Exception>{
    abstract Mono<Void> handlerException(final ServerWebExchange exchange, final T ex);

    private final ObjectMapper objectMapper;
    protected Mono<Void> writeResponse(ServerWebExchange exchange, ProblemResponse problemResponse) {
        return exchange.getResponse()
                .writeWith(Mono.fromCallable(() ->
                        new DefaultDataBufferFactory()
                                .wrap(objectMapper.writeValueAsBytes(problemResponse))));
    }

    protected void prepareExchange(final ServerWebExchange exchange, final HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(APPLICATION_JSON);
    }
    protected ProblemResponse buildError(final HttpStatus status, final String errorDescription) {
        return ProblemResponse.builder()
                .status(status.value())
                .errorDescription(errorDescription )
                .timestamp(OffsetDateTime.now())
                .build();
    }


}
