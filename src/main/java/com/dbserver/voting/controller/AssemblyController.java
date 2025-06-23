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

import com.dbserver.voting.dto.AssemblyDTO;
import com.dbserver.voting.service.IAssemblyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${path.assembly}")
public class AssemblyController {

    private static final Logger logger = LoggerFactory.getLogger(AssemblyController.class);

    private final IAssemblyService assemblyService;

    public AssemblyController(IAssemblyService assemblyService) {
        this.assemblyService = assemblyService;
    }

    @GetMapping
    public List<AssemblyDTO> getAllAssemblies() {
        logger.debug("Fetching all assemblies");
        return assemblyService.getAllAssemblies();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssemblyDTO> getAssemblyById(@PathVariable UUID id) {
        logger.debug("Fetching assembly by id: {}", id);
        Optional<AssemblyDTO> assembly = assemblyService.getAssemblyById(id);
        return assembly.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build()); 
    }

    @PostMapping
    public ResponseEntity<AssemblyDTO> createAssembly(@Valid @RequestBody AssemblyDTO assemblyDTO) {
        logger.debug("Creating new assembly: {}", assemblyDTO);
        AssemblyDTO registeredAssembly = assemblyService.registerAssembly(assemblyDTO);
        logger.info("Crated new assembly with id: {}", registeredAssembly.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredAssembly);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssemblyDTO> updateAssembly(@PathVariable UUID id, @Valid @RequestBody AssemblyDTO assemblyDTO) {
        logger.debug("Updating assembly with id: {}", id);
        AssemblyDTO updatedAssembly = assemblyService.updateAssembly(id, assemblyDTO);
        logger.info("Updated assembly with id: {}", id);
        return ResponseEntity.ok(updatedAssembly);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssembly(@PathVariable UUID id) {
        logger.debug("Deleting assembly with id: {}", id);
        assemblyService.deleteAssembly(id);
        logger.info("Removed assembly with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
