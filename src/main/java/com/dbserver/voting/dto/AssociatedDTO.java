package com.dbserver.voting.dto;

import java.util.UUID;

import com.dbserver.voting.model.Associated;

import jakarta.validation.constraints.NotNull;

public record AssociatedDTO(UUID id,
                            @NotNull(message = "Associated name is required")
                            String name,
                            @NotNull(message = "Associated email is required")
                            String email,
                            @NotNull(message = "Associated phone is required")
                            String phone,
                            Boolean active) {
    public Associated toEntity() {
        return Associated.builder()
                .id(id)
                .name(name)
                .email(email)
                .phone(phone)
                .active(active)
                .build();
    }
}