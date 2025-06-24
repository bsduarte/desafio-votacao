package com.dbserver.voting.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dbserver.voting.dto.ShortSubjectDTO;
import com.dbserver.voting.dto.SubjectDTO;
import com.dbserver.voting.dto.SubjectResultsDTO;
import com.dbserver.voting.service.ISubjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${path.subject}")
public class SubjectController {

    private static final Logger logger = LoggerFactory.getLogger(SubjectController.class);

    private final ISubjectService subjectService;

    public SubjectController(ISubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public List<SubjectDTO> getAllSubjects() {
        logger.debug("Fetching all subjects");
        return subjectService.getAllSubjects();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable UUID id) {
        logger.debug("Fetching subject by id: {}", id);
        Optional<SubjectDTO> subject = subjectService.getSubjectById(id);
        return subject.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<SubjectResultsDTO> getSubjectResultsById(@PathVariable UUID id) {
        logger.debug("Fetching subject results by id: {}", id);
        Optional<SubjectResultsDTO> subjectResults = subjectService.getSubjectResultsById(id);
        return subjectResults.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ShortSubjectDTO> createSubject(@Valid @RequestBody ShortSubjectDTO shortSubjectDTO) {
        logger.debug("Creating new subject: {}", shortSubjectDTO);
        ShortSubjectDTO registeredSubject = subjectService.registerSubject(shortSubjectDTO);
        logger.info("New subject created with id: {}", registeredSubject.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredSubject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShortSubjectDTO> updateSubject(@PathVariable UUID id, @Valid @RequestBody ShortSubjectDTO shortSubjectDTO) {
        logger.debug("Updating subject with id: {}", id);
        Optional<ShortSubjectDTO> updatedSubject = subjectService.updateSubject(id, shortSubjectDTO);
        logger.info("Updated subject with id: {}", id);
        return updatedSubject.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable UUID id) {
        logger.debug("Deleting subject with id: {}", id);
        subjectService.deleteSubject(id);
        logger.info("Removed subject with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
