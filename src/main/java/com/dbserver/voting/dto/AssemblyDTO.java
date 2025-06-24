package com.dbserver.voting.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.dbserver.voting.model.Assembly;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;

public record AssemblyDTO(UUID id,
                          @NotNull(message = "Assembly date is required")
                          @JsonFormat(pattern = "yyyy-MM-dd")
                          LocalDate assemblyDate) {
    public Assembly toEntity() {
        return Assembly.builder()
                .id(id)
                .assemblyDate(assemblyDate)
                .build();
    }
}
