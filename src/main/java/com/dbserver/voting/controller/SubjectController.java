package com.dbserver.voting.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dbserver.voting.model.SubjectDTO;
import com.dbserver.voting.model.SubjectResultsDTO;
import com.dbserver.voting.model.SubjectVotingDTO;
import com.dbserver.voting.service.ISubjectService;

@RestController
@RequestMapping("/subject")
public class SubjectController {

    private final ISubjectService subjectService;

    public SubjectController(ISubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public List<SubjectDTO> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable UUID id) {
        Optional<SubjectDTO> subject = subjectService.getSubjectById(id);
        return subject.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<SubjectResultsDTO> getSubjectResultsById(@PathVariable UUID id) {
        Optional<SubjectResultsDTO> subjectResults = subjectService.getSubjectResultsById(id);
        return subjectResults.map(ResponseEntity::ok)
                             .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public SubjectDTO createSubject(@RequestBody SubjectDTO subjectDTO) {
        return subjectService.registerSubject(subjectDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectDTO> updateSubject(@PathVariable UUID id, @RequestBody SubjectDTO subjectDTO) {
        try {
            SubjectDTO updatedSubject = subjectService.updateSubject(id, subjectDTO);
            return ResponseEntity.ok(updatedSubject);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable UUID id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}
