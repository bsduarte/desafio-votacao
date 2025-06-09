package com.dbserver.votacao.model;

import java.sql.Date;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Assembly {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Date day;

    public AssemblyDTO toDTO() {
        return new AssemblyDTO(id, day);
    }
}
