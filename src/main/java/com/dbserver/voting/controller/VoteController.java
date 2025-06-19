package com.dbserver.voting.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dbserver.voting.dto.ShortVoteDTO;
import com.dbserver.voting.dto.VoteDTO;
import com.dbserver.voting.service.IVoteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${path.vote}")
public class VoteController {

    private final IVoteService voteService;

    public VoteController(IVoteService voteService) {
        this.voteService = voteService;
    }

    @GetMapping
    public List<VoteDTO> getAllVotes() {
        return voteService.getAllVotes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoteDTO> getVoteById(@PathVariable UUID id) {
        Optional<VoteDTO> vote = voteService.getVoteById(id);
        return vote.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ShortVoteDTO> createVote(@Valid @RequestBody ShortVoteDTO shortVoteDTO) {
        ShortVoteDTO registeredVote = voteService.registerVote(shortVoteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredVote);
    }
}
