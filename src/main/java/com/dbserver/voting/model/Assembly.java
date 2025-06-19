package com.dbserver.voting.model;

import java.time.LocalDate;
import java.util.UUID;

import com.dbserver.voting.dto.AssemblyDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assembly {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = false, updatable = false)
    private UUID id;
    private LocalDate assemblyDate;

    public AssemblyDTO toDTO() {
        return new AssemblyDTO(id, assemblyDate);
    }
}
