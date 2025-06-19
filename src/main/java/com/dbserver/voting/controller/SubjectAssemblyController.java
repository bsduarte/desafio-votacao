package com.dbserver.voting.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dbserver.voting.dto.ShortSubjectAssemblyDTO;
import com.dbserver.voting.dto.SubjectAssemblyDTO;
import com.dbserver.voting.service.ISubjectAssemblyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${path.subject-assembly}")
public class SubjectAssemblyController {

    private final ISubjectAssemblyService subjectAssemblyService;
    public SubjectAssemblyController(ISubjectAssemblyService subjectAssemblyService) {
        this.subjectAssemblyService = subjectAssemblyService;
    }

    @GetMapping
    public List<SubjectAssemblyDTO> getAllSubjectAssemblies() {
        return subjectAssemblyService.getAllSubjectAssemblies();
    }

    @PostMapping
    public ResponseEntity<ShortSubjectAssemblyDTO> registerSubjectAssembly(@Valid @RequestBody ShortSubjectAssemblyDTO shortSubjectAssemblyDTO) {
        ShortSubjectAssemblyDTO registeredSubjectAssembly = subjectAssemblyService.registerSubjectAssembly(shortSubjectAssemblyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredSubjectAssembly);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSubjectAssembly(@Valid @RequestBody ShortSubjectAssemblyDTO shortSubjectAssemblyDTO) {
        subjectAssemblyService.deleteSubjectAssembly(shortSubjectAssemblyDTO);
        return ResponseEntity.noContent().build();
    }
}
