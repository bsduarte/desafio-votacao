package com.dbserver.voting.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dbserver.voting.dto.ShortVoteDTO;
import com.dbserver.voting.dto.VoteDTO;
import com.dbserver.voting.model.Vote;
import com.dbserver.voting.model.Voting;
import com.dbserver.voting.repository.IVoteRepository;

@Service
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
    public ShortVoteDTO registerVote(ShortVoteDTO shortVoteDTO) {
        Vote vote = shortVoteDTO.toEntity();
        Vote savedVote = voteRepository.save(vote);
        return savedVote.toShortDTO();
    }
}
