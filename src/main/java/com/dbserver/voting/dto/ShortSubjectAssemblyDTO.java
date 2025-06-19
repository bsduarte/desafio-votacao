package com.dbserver.voting.dto;

import java.util.UUID;

import com.dbserver.voting.domain.SubjectAssemblyId;
import com.dbserver.voting.model.Assembly;
import com.dbserver.voting.model.Subject;
import com.dbserver.voting.model.SubjectAssembly;

import jakarta.validation.constraints.NotNull;

public record ShortSubjectAssemblyDTO(@NotNull(message = "Subject is required")
                                    UUID subject,
                                    @NotNull(message = "Assembly is required")
                                    UUID assembly) {
    public SubjectAssembly toEntity() {
        return SubjectAssembly.builder()
                .id(SubjectAssemblyId.of(subject, assembly))
                .subject(Subject.builder().id(subject).build())
                .assembly(Assembly.builder().id(assembly).build())
                .build();
    }
}
