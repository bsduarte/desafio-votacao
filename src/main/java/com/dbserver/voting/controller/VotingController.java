package com.dbserver.voting.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dbserver.voting.dto.ShortVotingDTO;
import com.dbserver.voting.dto.VotingDTO;
import com.dbserver.voting.service.IVotingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${path.voting}")
public class VotingController {

    private static final Logger logger = LoggerFactory.getLogger(VotingController.class);

    private final IVotingService votingService;

    public VotingController(IVotingService votingService) {
        this.votingService = votingService;
    }

    @GetMapping
    public List<VotingDTO> getAllVoting() {
        logger.debug("Fetching all voting");
        return votingService.getAllVoting();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VotingDTO> getVotingById(@PathVariable UUID id) {
        logger.debug("Fetching voting by id: {}", id);
        Optional<VotingDTO> voting = votingService.getVotingById(id);
        return voting.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ShortVotingDTO> createVoting(@Valid @RequestBody ShortVotingDTO shortVotingDTO) {
        logger.debug("Creating new voting: {}", shortVotingDTO);
        ShortVotingDTO registeredVoting = votingService.registerVoting(shortVotingDTO);
        logger.info("Created new voting with id: {}", registeredVoting.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredVoting);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShortVotingDTO> updateVoting(@PathVariable UUID id, @Valid @RequestBody ShortVotingDTO shortVotingDTO) {
        logger.debug("Updating voting with id: {}", id);
        Optional<ShortVotingDTO> updatedVoting = votingService.updateVoting(id, shortVotingDTO);
        logger.info("Updated voting with id: {}", id);
        return updatedVoting.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<Void> closeVoting(@PathVariable UUID id) {
        logger.debug("Closing voting with id: {}", id);
        if (votingService.closeVoting(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        logger.info("Closed voting with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelVoting(@PathVariable UUID id) {
        logger.debug("Canceling voting with id: {}", id);
        if (votingService.cancelVoting(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        logger.info("Canceled voting with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
