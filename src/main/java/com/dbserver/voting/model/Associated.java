package com.dbserver.voting.model;

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
public class Associated {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = false, updatable = false)
    private UUID id;
    private String name;
    private String email;
    private String phone;
    @Column(insertable = false, updatable = true)
    private Boolean active = true;

    public AssociatedDTO toDTO() {
        return new AssociatedDTO(id, name, email, phone, active);
    }
}
