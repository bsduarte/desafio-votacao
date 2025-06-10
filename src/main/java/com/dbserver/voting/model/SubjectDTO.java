package com.dbserver.voting.model;

import java.util.UUID;

public record SubjectDTO(UUID id, String headline, String description) {
    public Subject toEntity() {
        return new Subject(id, headline, description);
    }

    public Subject toNewEntity() {
        return new Subject(null, headline, description);
    }
}
