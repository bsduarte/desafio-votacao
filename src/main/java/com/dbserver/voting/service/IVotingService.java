package com.dbserver.voting.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.dbserver.voting.domain.VotingStatus;
import com.dbserver.voting.dto.ShortVotingDTO;
import com.dbserver.voting.dto.VotingDTO;
import com.dbserver.voting.model.Subject;

public interface IVotingService {
    /**
     * Retrieves all voting.
     *
     * @return a list of all voting
     */
    List<VotingDTO> getAllVoting();

    /**
     * Retrieves a voting by their ID.
     *
     * @param id the ID of the voting
     * @return the voting with the given ID
     */
    Optional<VotingDTO> getVotingById(UUID id);

    /**
     * Retrieves voting by status.
     * @param status
     * @return
     */
    List<VotingDTO> getVotingByStatus(VotingStatus status);

    /**
     * Retrieves voting by subject ID.
     *
     * @param subjectId the ID of the subject
     * @return a list of voting associated with the given subject ID
     */
    List<VotingDTO> getVotingBySubject(Subject subject);

    /**
     * Registers a new voting.
     *
     * @param shortVotingDTO the voting to register
     * @return the registered voting
     */
    ShortVotingDTO registerVoting(ShortVotingDTO shortVotingDTO);

    /**
     * Updates an existing voting.
     *
     * @param id the ID of the voting to update
     * @param votingDTO the new data for the voting
     * @return the updated voting
     */
    Optional<ShortVotingDTO> updateVoting(UUID id, ShortVotingDTO votingDTO);

    /**
     * Closes a voting by their ID.
     *
     * @param id the ID of the voting to close
     */
    Optional<ShortVotingDTO> closeVoting(UUID id);

    /**
     * Cancels a voting by their ID.
     *
     * @param id the ID of the voting to cancel
     */
    Optional<ShortVotingDTO> cancelVoting(UUID id);
}
