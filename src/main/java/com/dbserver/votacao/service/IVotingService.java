package com.dbserver.votacao.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.dbserver.votacao.model.Subject;
import com.dbserver.votacao.model.VotingDTO;
import com.dbserver.votacao.model.VotingStatus;

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
     * @param votingDTO the voting to register
     * @return the registered voting
     */
    VotingDTO registerVoting(VotingDTO votingDTO);

    /**
     * Updates an existing voting.
     *
     * @param id the ID of the voting to update
     * @param votingDTO the new data for the voting
     * @return the updated voting
     */
    VotingDTO updateVoting(UUID id, VotingDTO votingDTO);

    /**
     * Deletes a voting by their ID.
     *
     * @param id the ID of the voting to delete
     */
    void deleteVoting(UUID id);
}
