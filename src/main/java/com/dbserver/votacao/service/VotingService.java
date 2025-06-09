package com.dbserver.votacao.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.dbserver.votacao.model.Subject;
import com.dbserver.votacao.model.Voting;
import com.dbserver.votacao.model.VotingDTO;
import com.dbserver.votacao.model.VotingStatus;
import com.dbserver.votacao.repository.IVotingRepository;

public class VotingService implements IVotingService {

    private final IVotingRepository votingRepository;

    public VotingService(IVotingRepository votingRepository) {
        this.votingRepository = votingRepository;
    }

    @Override
    public List<VotingDTO> getAllVoting() {
        return votingRepository.findAll().stream()
                .map(Voting::toDTO)
                .toList();
    }

    @Override
    public Optional<VotingDTO> getVotingById(UUID id) {
        return votingRepository.findById(id)
                .map(Voting::toDTO);
    }

    @Override
    public List<VotingDTO> getVotingByStatus(VotingStatus status) {
        return votingRepository.findByStatus(status).stream()
                .map(Voting::toDTO)
                .toList();
    }

    @Override
    public List<VotingDTO> getVotingBySubject(Subject subject) {
        return votingRepository.findBySubject(subject).stream()
                .map(Voting::toDTO)
                .toList();
    }

    @Override
    public VotingDTO registerVoting(VotingDTO votingDTO) {
        Voting voting = votingDTO.toEntity();
        Voting savedVoting = votingRepository.save(voting);
        return savedVoting.toDTO();
    }

    @Override
    public VotingDTO updateVoting(UUID id, VotingDTO votingDTO) {
        Voting voting = votingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voting not found with id: " + id));
        voting.setSubject(votingDTO.subject());
        voting.setVotingInterval(votingDTO.votingInterval());
        voting.setOpenedIn(votingDTO.openedIn());
        voting.setClosesIn(votingDTO.closesIn());
        voting.setStatus(votingDTO.status());
        voting.setResult(votingDTO.result());
        voting.setVotesInFavor(votingDTO.votesInFavor());
        voting.setVotesAgainst(votingDTO.votesAgainst());
        Voting updatedVoting = votingRepository.save(voting);
        return updatedVoting.toDTO();
    }

    @Override
    public void deleteVoting(UUID id) {
        votingRepository.deleteById(id);
    }
}
