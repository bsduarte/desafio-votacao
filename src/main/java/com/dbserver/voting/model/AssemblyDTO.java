package com.dbserver.voting.model;

import java.sql.Date;
import java.util.UUID;

public record AssemblyDTO(UUID id, Date day) {
    public Assembly toEntity() {
        return new Assembly(id, day);
    }

    public Assembly toNewEntity() {
        return new Assembly(null, day);
    }
}
