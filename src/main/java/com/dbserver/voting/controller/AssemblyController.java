package com.dbserver.voting.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    private final IAssemblyService assemblyService;

    public AssemblyController(IAssemblyService assemblyService) {
        this.assemblyService = assemblyService;
    }

    @GetMapping
    public List<AssemblyDTO> getAllAssemblies() {
        return assemblyService.getAllAssemblies();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssemblyDTO> getAssemblyById(@PathVariable UUID id) {
        Optional<AssemblyDTO> assembly = assemblyService.getAssemblyById(id);
        return assembly.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build()); 
    }

    @PostMapping
    public ResponseEntity<AssemblyDTO> createAssembly(@Valid @RequestBody AssemblyDTO assemblyDTO) {
        AssemblyDTO registeredAssembly = assemblyService.registerAssembly(assemblyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredAssembly);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssemblyDTO> updateAssembly(@PathVariable UUID id, @Valid @RequestBody AssemblyDTO assemblyDTO) {
        try {
            AssemblyDTO updatedAssembly = assemblyService.updateAssembly(id, assemblyDTO);
            return ResponseEntity.ok(updatedAssembly);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssembly(@PathVariable UUID id) {
        assemblyService.deleteAssembly(id);
        return ResponseEntity.noContent().build();
    }
}
