package com.dbserver.voting.domain;

import java.util.Objects;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
}
