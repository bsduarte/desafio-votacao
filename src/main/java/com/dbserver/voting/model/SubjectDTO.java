package com.dbserver.voting.model;

import java.util.List;
import java.util.UUID;

public record SubjectDTO(UUID id, String headline, String description, List<AssemblyDTO> assemblies) {
    public Subject toEntity() {
        return new Subject(id, headline, description);
    }
}
