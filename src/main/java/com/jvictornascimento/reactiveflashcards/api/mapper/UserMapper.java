package com.jvictornascimento.reactiveflashcards.api.mapper;

import com.jvictornascimento.reactiveflashcards.api.controller.request.UserRequest;
import com.jvictornascimento.reactiveflashcards.api.controller.response.UserResponse;
import com.jvictornascimento.reactiveflashcards.domain.document.UserDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    UserDocument toDocumento(final UserRequest request);

    UserResponse toResponse(final UserDocument document);
}
