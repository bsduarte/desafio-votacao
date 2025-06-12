package com.dbserver.voting.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dbserver.voting.model.VotingDTO;
import com.dbserver.voting.service.IVotingService;

@RestController
@RequestMapping("/voting")
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
    public VotingDTO createVoting(@RequestBody VotingDTO votingDTO) {
        return votingService.registerVoting(votingDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VotingDTO> updateVoting(@PathVariable UUID id, @RequestBody VotingDTO votingDTO) {
        try {
            VotingDTO updatedVoting = votingService.updateVoting(id, votingDTO);
            return ResponseEntity.ok(updatedVoting);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<Void> closeVoting(@PathVariable UUID id) {
        try {
            votingService.closeVoting(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelVoting(@PathVariable UUID id) {
        try {
            votingService.cancelVoting(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
