package com.dbserver.votacao.model;

import java.util.UUID;

public record SubjectDTO(UUID id, String headline, String description) {
    public Subject toEntity() {
        return new Subject(id, headline, description);
    }
}
