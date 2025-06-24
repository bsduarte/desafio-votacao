package com.dbserver.voting.domain;

import java.util.Objects;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectAssemblyId {
    private UUID subject;
    private UUID assembly;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubjectAssemblyId)) return false;
        SubjectAssemblyId that = (SubjectAssemblyId) o;
        return getSubject().equals(that.getSubject()) && getAssembly().equals(that.getAssembly());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubject(), getAssembly());
    }

    public static SubjectAssemblyId of(UUID subject, UUID assembly) {
        return SubjectAssemblyId.builder()
                .subject(subject)
                .assembly(assembly)
                .build();
    }
}
