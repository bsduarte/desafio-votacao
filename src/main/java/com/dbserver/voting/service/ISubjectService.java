package com.dbserver.voting.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.dbserver.voting.model.SubjectDTO;
import com.dbserver.voting.model.SubjectResultsDTO;

public interface ISubjectService {
    /**
     * Retrieves all subjects.
     *
     * @return a list of all subjects
     */
    List<SubjectDTO> getAllSubjects();

    /**
     * Retrieves an subject by their ID.
     *
     * @param id the ID of the subject
     * @return the subject with the given ID
     */
    Optional<SubjectDTO> getSubjectById(UUID id);

    /**
     * Retrieves an subject and their results by their ID.
     *
     * @param id the ID of the subject
     * @return the subject with the given ID
     */
    Optional<SubjectResultsDTO> getSubjectResultsById(UUID id);

    /**
     * Registers a new subject.
     *
     * @param subjectDTO the subject to register
     * @return the registered subject
     */
    SubjectDTO registerSubject(SubjectDTO subjectDTO);

    /**
     * Updates an existing subject.
     *
     * @param id the ID of the subject to update
     * @param subjectDTO the new data for the subject
     * @return the updated subject
     */
    SubjectDTO updateSubject(UUID id, SubjectDTO subjectDTO);

    /**
     * Deletes an subject by their ID.
     *
     * @param id the ID of the subject to delete
     */
    void deleteSubject(UUID id);
}
