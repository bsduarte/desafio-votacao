package com.dbserver.voting.dto;

import java.util.List;
import java.util.UUID;

import com.dbserver.voting.model.Subject;

import jakarta.validation.constraints.NotNull;

public record SubjectDTO(UUID id,
                         @NotNull(message = "Subject headline is required")
                         String headline,
                         @NotNull(message = "Subject description is required")
                         String description,
                         List<AssemblyDTO> assemblies) {

    public Subject toEntity() {
        return Subject.builder()
                .id(id)
                .headline(headline)
                .description(description)
                .build();
    }

    public ShortSubjectDTO toShortDTO() {
        return new ShortSubjectDTO(id, headline, description);
    }
}
