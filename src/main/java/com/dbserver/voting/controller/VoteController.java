package com.dbserver.voting.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dbserver.voting.model.VoteDTO;
import com.dbserver.voting.service.IVoteService;

@RestController
@RequestMapping("/vote")
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
    public VoteDTO createVote(@RequestBody VoteDTO voteDTO) {
        return voteService.registerVote(voteDTO);
    }
}
