package com.dbserver.voting.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dbserver.voting.domain.VotingStatus;
import com.dbserver.voting.dto.ShortVotingDTO;
import com.dbserver.voting.dto.VotingDTO;
import com.dbserver.voting.model.Subject;
import com.dbserver.voting.model.Voting;
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
    public ShortVotingDTO registerVoting(ShortVotingDTO shortVotingDTO) {
        Voting voting = shortVotingDTO.toEntity();
        Voting savedVoting = votingRepository.save(voting);
        return savedVoting.toShortDTO();
    }

    @Override
    public ShortVotingDTO updateVoting(UUID id, ShortVotingDTO shortVotingDTO) {
        if (!votingRepository.existsById(id)) {
            throw new RuntimeException("Voting not found with id: " + id);
        }
        Voting voting = shortVotingDTO.toEntity();
        voting.setId(id);
        return votingRepository.save(voting).toShortDTO();
    }

    @Override
    public void closeVoting(UUID id) {
        Voting voting = votingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voting not found with id: " + id));
        voting.setStatus(VotingStatus.CLOSED);
        votingRepository.save(voting);
    }

    @Override
    public void cancelVoting(UUID id) {
        Voting voting = votingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voting not found with id: " + id));
        voting.setStatus(VotingStatus.CANCELLED);
        votingRepository.save(voting);
    }

    @Override
    public void deleteVoting(UUID id) {
        votingRepository.deleteById(id);
    }
}
