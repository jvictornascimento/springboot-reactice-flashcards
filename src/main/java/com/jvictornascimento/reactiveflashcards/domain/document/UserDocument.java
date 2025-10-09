package com.jvictornascimento.reactiveflashcards.domain.document;

import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;

@Document(collection = "users")
public record UserDocument(@Id String id,
                           String name,
                           String email,
                           @CreatedDate
                           @Field("create_at")
                           OffsetDateTime createAt,
                           @LastModifiedDate
                           @Field("update_at")
                           OffsetDateTime updateAt) {
    @Builder(toBuilder = true)
    public UserDocument{}
}
