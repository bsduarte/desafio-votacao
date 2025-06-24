package com.dbserver.voting.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import jakarta.ws.rs.NotFoundException;

@RestController
@RequestMapping("${path.subject-assembly}")
public class SubjectAssemblyController {

    private static final Logger logger = LoggerFactory.getLogger(SubjectAssemblyController.class);

    private final ISubjectAssemblyService subjectAssemblyService;
    public SubjectAssemblyController(ISubjectAssemblyService subjectAssemblyService) {
        this.subjectAssemblyService = subjectAssemblyService;
    }

    @GetMapping
    public List<SubjectAssemblyDTO> getAllSubjectAssemblies() {
        logger.debug("Fetching all subject-assembly");
        return subjectAssemblyService.getAllSubjectAssemblies();
    }

    @PostMapping
    public ResponseEntity<ShortSubjectAssemblyDTO> registerSubjectAssembly(@Valid @RequestBody ShortSubjectAssemblyDTO shortSubjectAssemblyDTO) {
        logger.debug("Registering new subject-assembly association: {}:{}",
                    shortSubjectAssemblyDTO.subject(), shortSubjectAssemblyDTO.assembly());
        ShortSubjectAssemblyDTO registeredSubjectAssembly = subjectAssemblyService.registerSubjectAssembly(shortSubjectAssemblyDTO);
        logger.info("Registered new subject-assembly association: {}:{}",
                    registeredSubjectAssembly.subject(), registeredSubjectAssembly.assembly());
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredSubjectAssembly);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSubjectAssembly(@Valid @RequestBody ShortSubjectAssemblyDTO shortSubjectAssemblyDTO) {
        logger.debug("Deleting subject-assembly association: {}:{}",
                    shortSubjectAssemblyDTO.subject(), shortSubjectAssemblyDTO.assembly());
        try {
            subjectAssemblyService.deleteSubjectAssembly(shortSubjectAssemblyDTO);
            logger.info("Removed subject-assembly association: {}:{}",
                    shortSubjectAssemblyDTO.subject(), shortSubjectAssemblyDTO.assembly());
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        
    }
}
