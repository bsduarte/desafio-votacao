package com.dbserver.voting.dto;

import com.dbserver.voting.domain.SubjectAssemblyId;
import com.dbserver.voting.model.SubjectAssembly;

import jakarta.validation.constraints.NotNull;

public record SubjectAssemblyDTO(@NotNull(message = "Subject is required")
                                SubjectDTO subject,
                                @NotNull(message = "Subject is required")
                                AssemblyDTO assembly) {

    public SubjectAssembly toEntity() {
        return SubjectAssembly.builder()
                .id(SubjectAssemblyId.of(subject.id(), assembly.id()))
                .subject(subject.toEntity())
                .assembly(assembly.toEntity())
                .build();
    }

    public ShortSubjectAssemblyDTO toShortDTO() {
        return new ShortSubjectAssemblyDTO(subject.id(), assembly.id());
    }
}
