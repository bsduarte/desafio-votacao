package com.dbserver.voting.service;

import java.util.List;

import com.dbserver.voting.dto.ShortSubjectAssemblyDTO;
import com.dbserver.voting.dto.SubjectAssemblyDTO;

public interface ISubjectAssemblyService {
    /**
     * Retrieves all subject-assembly associations.
     *
     * @return a list of all subject-assembly associations
     */
    List<SubjectAssemblyDTO> getAllSubjectAssemblies();

    /**
     * Registers a new assembly/subject association.
     *
     * @param subjectAssemblyDTO the subject to register
     * @return the registered subject
     */
    ShortSubjectAssemblyDTO registerSubjectAssembly(ShortSubjectAssemblyDTO subjectAssemblyDTO);

    /**
     * Dissociates a subject from an assembly.
     *
     * @param subjectAssemblyDTO the subject-assembly association to delete
     */
    void deleteSubjectAssembly(ShortSubjectAssemblyDTO subjectAssemblyDTO);
}
