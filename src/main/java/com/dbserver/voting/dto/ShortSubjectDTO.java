package com.dbserver.voting.dto;

import java.util.UUID;

import com.dbserver.voting.model.Subject;

import jakarta.validation.constraints.NotNull;

public record ShortSubjectDTO(UUID id,
                            @NotNull(message = "Subject headline is required")
                            String headline,
                            @NotNull(message = "Subject description is required")
                            String description) {
    public Subject toEntity() {
        return Subject.builder()
                        .id(id)
                        .headline(headline)
                        .description(description)
                        .build();
    }
}
