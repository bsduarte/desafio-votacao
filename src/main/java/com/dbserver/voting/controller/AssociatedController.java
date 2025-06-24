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

import com.dbserver.voting.dto.AssociatedDTO;
import com.dbserver.voting.service.IAssociatedService;

import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;

@RestController
@RequestMapping("${path.associated}")
public class AssociatedController {

    private static final Logger logger = LoggerFactory.getLogger(AssociatedController.class);

    private final IAssociatedService associatedService;

    public AssociatedController(IAssociatedService associatedService) {
        this.associatedService = associatedService;
    }

    @GetMapping
    public List<AssociatedDTO> getAllAssociated() {
        logger.debug("Fetching all associated");
        return associatedService.getAllAssociated();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssociatedDTO> getAssociatedById(@PathVariable UUID id) {
        logger.debug("Fetching associated by id: {}", id);
        Optional<AssociatedDTO> associated = associatedService.getAssociatedById(id);
        return associated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AssociatedDTO> registerAssociated(@Valid @RequestBody AssociatedDTO associatedDTO) {
        logger.debug("Registering new associated: {}", associatedDTO);
        AssociatedDTO registeredAssociated = associatedService.registerAssociated(associatedDTO);
        logger.info("Registered new associated with id: {}", registeredAssociated.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredAssociated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssociatedDTO> updateAssociated(@PathVariable UUID id, @Valid @RequestBody AssociatedDTO associatedDTO) {
        logger.debug("Updating associated with id: {}", id);
        Optional<AssociatedDTO> updatedAssociated = associatedService.updateAssociated(id, associatedDTO);
        logger.info("Updated associated with id: {}", id);
        return updatedAssociated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssociated(@PathVariable UUID id) {
        logger.debug("Deleting associated with id: {}", id);
        try {
            associatedService.deleteAssociated(id);
            logger.info("Removed associated with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
