package com.dbserver.voting.model;

import com.dbserver.voting.domain.SubjectAssemblyId;

public record SubjectAssemblyDTO(Subject subject, Assembly assembly) {
    public SubjectAssembly toEntity() {
        return new SubjectAssembly(new SubjectAssemblyId(subject.getId(), assembly.getId()), subject, assembly);
    }
}
