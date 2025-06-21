package com.dbserver.voting.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    private final IVotingService votingService;

    public VotingController(IVotingService votingService) {
        this.votingService = votingService;
    }

    @GetMapping
    public List<VotingDTO> getAllVoting() {
        return votingService.getAllVoting();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VotingDTO> getVotingById(@PathVariable UUID id) {
        Optional<VotingDTO> voting = votingService.getVotingById(id);
        return voting.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ShortVotingDTO> createVoting(@Valid @RequestBody ShortVotingDTO shortVotingDTO) {
        ShortVotingDTO registeredVoting = votingService.registerVoting(shortVotingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredVoting);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShortVotingDTO> updateVoting(@PathVariable UUID id, @Valid @RequestBody ShortVotingDTO shortVotingDTO) {
        ShortVotingDTO updatedVoting = votingService.updateVoting(id, shortVotingDTO);
        return ResponseEntity.ok(updatedVoting);
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<Void> closeVoting(@PathVariable UUID id) {
        votingService.closeVoting(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelVoting(@PathVariable UUID id) {
        votingService.cancelVoting(id);
        return ResponseEntity.noContent().build();
    }
}
