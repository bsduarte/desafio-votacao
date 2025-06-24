package com.dbserver.voting.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.dbserver.voting.dto.ShortVoteDTO;
import com.dbserver.voting.dto.VoteDTO;
import com.dbserver.voting.model.Voting;

public interface IVoteService {
    /**
     * Retrieves all votes.
     *
     * @return a list of all votes
     */
    List<VoteDTO> getAllVotes();

    /**
     * Retrieves a vote by their ID.
     *
     * @param id the ID of the vote
     * @return the vote with the given ID
     */
    Optional<VoteDTO> getVoteById(UUID id);

    /**
     * Retrieves votes by voting ID.
     *
     * @param votingId the ID of the voting
     * @return a list of votes associated with the given voting ID
     */
    List<VoteDTO> getVotesByVoting(Voting voting);

    /**
     * Registers a new vote.
     *
     * @param voteDTO the vote to register
     * @return the registered vote
     */
    ShortVoteDTO registerVote(ShortVoteDTO voteDTO);
}
