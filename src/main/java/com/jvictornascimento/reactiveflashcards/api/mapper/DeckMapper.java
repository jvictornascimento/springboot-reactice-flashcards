package com.jvictornascimento.reactiveflashcards.api.mapper;

import com.jvictornascimento.reactiveflashcards.api.controller.request.DeckRequest;
import com.jvictornascimento.reactiveflashcards.api.controller.response.DeckResponse;
import com.jvictornascimento.reactiveflashcards.domain.document.DeckDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface DeckMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    DeckDocument toDocumento(final DeckRequest request);
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    DeckDocument toDocumento(final DeckRequest request, String id);

    DeckResponse toResponse(final DeckDocument document);
}
