package com.dbserver.voting.model;

import java.util.UUID;

import com.dbserver.voting.dto.ShortSubjectDTO;
import com.dbserver.voting.dto.SubjectDTO;

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
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = false, updatable = false)
    private UUID id;
    private String headline;
    private String description;

    public SubjectDTO toDTO() {
        return new SubjectDTO(id, headline, description, null);
    }

    public ShortSubjectDTO toShortDTO() {
        return new ShortSubjectDTO(id, headline, description);
    }
}
