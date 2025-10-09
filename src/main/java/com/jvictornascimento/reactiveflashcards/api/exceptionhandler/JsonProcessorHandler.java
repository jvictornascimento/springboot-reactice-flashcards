package com.jvictornascimento.reactiveflashcards.api.exceptionhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.jvictornascimento.reactiveflashcards.domain.exception.BaseErrorMessage.GENERIC_METHOD_NOT_ALLOW;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
@Slf4j
@Component
public class JsonProcessorHandler extends AbstractHandlerException<JsonProcessingException>{
    public JsonProcessorHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    Mono<Void> handlerException(ServerWebExchange exchange, JsonProcessingException ex) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, METHOD_NOT_ALLOWED);
                    return GENERIC_METHOD_NOT_ALLOW.getMessage();
                }).map(message -> buildError(METHOD_NOT_ALLOWED, message))
                .doFirst(() -> log.error("==== JsonProcessingException: Failed to map exception for the request {} "
                        ,exchange.getRequest().getPath().value(), ex))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }
}
