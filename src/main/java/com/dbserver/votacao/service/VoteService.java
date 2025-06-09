package com.dbserver.votacao.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.dbserver.votacao.model.Vote;
import com.dbserver.votacao.model.VoteDTO;
import com.dbserver.votacao.model.Voting;
import com.dbserver.votacao.repository.IVoteRepository;

public class VoteService implements IVoteService {

    private final IVoteRepository voteRepository;

    public VoteService(IVoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @Override
    public List<VoteDTO> getAllVotes() {
        return voteRepository.findAll().stream()
                .map(Vote::toDTO)
                .toList();
    }

    @Override
    public Optional<VoteDTO> getVoteById(UUID id) {
        return voteRepository.findById(id)
                .map(Vote::toDTO);
    }

    @Override
    public List<VoteDTO> getVotesByVoting(Voting voting) {
        return voteRepository.findByVoting(voting).stream()
                .map(Vote::toDTO)
                .toList();
    }

    @Override
    public VoteDTO registerVote(VoteDTO voteDTO) {
        Vote vote = voteDTO.toEntity();
        Vote savedVote = voteRepository.save(vote);
        return savedVote.toDTO();
    }
}
