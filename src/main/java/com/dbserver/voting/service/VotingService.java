package com.dbserver.voting.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dbserver.voting.model.Subject;
import com.dbserver.voting.model.Voting;
import com.dbserver.voting.model.VotingDTO;
import com.dbserver.voting.model.VotingStatus;
import com.dbserver.voting.repository.IVotingRepository;

@Service
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
    public void closeVoting(UUID id) {
        Voting voting = votingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voting not found with id: " + id));
        voting.setStatus(VotingStatus.closed);
        votingRepository.save(voting);
    }

    @Override
    public void cancelVoting(UUID id) {
        Voting voting = votingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voting not found with id: " + id));
        voting.setStatus(VotingStatus.canceled);
        votingRepository.save(voting);
    }

    @Override
    public void deleteVoting(UUID id) {
        votingRepository.deleteById(id);
    }
}
