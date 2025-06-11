package com.dbserver.voting.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Assembly {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = false, updatable = false)
    private UUID id;
    private LocalDate day;

    public AssemblyDTO toDTO() {
        return new AssemblyDTO(id, day);
    }
}
