package com.dbserver.voting.model;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

public record AssemblyDTO(
    UUID id,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate day) {

    public Assembly toEntity() {
        return new Assembly(id, day);
    }
}
