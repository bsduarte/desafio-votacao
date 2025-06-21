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

import com.dbserver.voting.dto.AssociatedDTO;
import com.dbserver.voting.service.IAssociatedService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${path.associated}")
public class AssociatedController {

    private final IAssociatedService associatedService;

    public AssociatedController(IAssociatedService associatedService) {
        this.associatedService = associatedService;
    }

    @GetMapping
    public List<AssociatedDTO> getAllAssociated() {
        return associatedService.getAllAssociated();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssociatedDTO> getAssociatedById(@PathVariable UUID id) {
        Optional<AssociatedDTO> associated = associatedService.getAssociatedById(id);
        return associated.map(ResponseEntity::ok)
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AssociatedDTO> registerAssociated(@Valid @RequestBody AssociatedDTO associatedDTO) {
        AssociatedDTO registeredAssociated = associatedService.registerAssociated(associatedDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredAssociated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssociatedDTO> updateAssociated(@PathVariable UUID id, @Valid @RequestBody AssociatedDTO associatedDTO) {
        AssociatedDTO updatedAssociated = associatedService.updateAssociated(id, associatedDTO);
        return ResponseEntity.ok(updatedAssociated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssociated(@PathVariable UUID id) {
        associatedService.deleteAssociated(id);
        return ResponseEntity.noContent().build();
    }
}
