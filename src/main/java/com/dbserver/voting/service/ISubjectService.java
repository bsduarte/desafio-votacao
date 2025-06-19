package com.dbserver.voting.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.dbserver.voting.dto.ShortSubjectDTO;
import com.dbserver.voting.dto.SubjectDTO;
import com.dbserver.voting.dto.SubjectResultsDTO;

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
     * @param shortSubjectDTO the subject to register
     * @return the registered subject
     */
    ShortSubjectDTO registerSubject(ShortSubjectDTO shortSubjectDTO);

    /**
     * Updates an existing subject.
     *
     * @param id the ID of the subject to update
     * @param subjectDTO the new data for the subject
     * @return the updated subject
     */
    ShortSubjectDTO updateSubject(UUID id, ShortSubjectDTO shortSubjectDTO);

    /**
     * Deletes an subject by their ID.
     *
     * @param id the ID of the subject to delete
     */
    void deleteSubject(UUID id);
}
