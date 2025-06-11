package com.dbserver.voting.model;

import java.util.UUID;

public record AssociatedDTO(UUID id, String name, String email, String phone) {
    public Associated toEntity() {
        return new Associated(id, name, email, phone);
    }
}