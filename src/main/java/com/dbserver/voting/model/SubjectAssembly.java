package com.dbserver.voting.model;

import com.dbserver.voting.domain.SubjectAssemblyId;
import com.dbserver.voting.dto.ShortSubjectAssemblyDTO;
import com.dbserver.voting.dto.SubjectAssemblyDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
public class SubjectAssembly {
    @EmbeddedId
    private SubjectAssemblyId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("subject")
    @JoinColumn(name = "subject", nullable = false, updatable = false)
    @JsonBackReference
    private Subject subject;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("assembly")
    @JoinColumn(name = "assembly", nullable = false, updatable = false)
    @JsonBackReference
    private Assembly assembly;

    public SubjectAssemblyDTO toDTO() {
        return new SubjectAssemblyDTO(subject.toDTO(), assembly.toDTO());
    }

    public ShortSubjectAssemblyDTO toShortDTO() {
        return new ShortSubjectAssemblyDTO(subject.getId(), assembly.getId());
    }
}
