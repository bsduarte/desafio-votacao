package com.dbserver.voting.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dbserver.voting.domain.VotingStatus;
import com.dbserver.voting.dto.ShortVotingDTO;
import com.dbserver.voting.dto.VotingDTO;
import com.dbserver.voting.model.Subject;
import com.dbserver.voting.model.Voting;
import com.dbserver.voting.repository.IVotingRepository;

@Service
public class VotingService implements IVotingService {

    private static final Logger logger = LoggerFactory.getLogger(VotingService.class);

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
    public Optional<ShortVotingDTO> updateVoting(UUID id, ShortVotingDTO shortVotingDTO) {
        if (!votingRepository.existsById(id)) {
            logger.warn("Voting not found with id: {}", id);
            return Optional.empty();
        }
        Voting voting = shortVotingDTO.toEntity();
        voting.setId(id);
        return Optional.of(votingRepository.save(voting).toShortDTO());
    }

    @Override
    public Optional<ShortVotingDTO> closeVoting(UUID id) {
        Optional<Voting> votingOpt = votingRepository.findById(id);
        if (votingOpt.isEmpty()) {
            logger.warn("Voting not found with id: {}", id);
            return Optional.empty();
        }
        Voting voting = votingOpt.get();
        voting.setStatus(VotingStatus.CLOSED);
        return Optional.of(votingRepository.save(voting).toShortDTO());
    }

    @Override
    public Optional<ShortVotingDTO> cancelVoting(UUID id) {
        Optional<Voting> votingOpt = votingRepository.findById(id);
        if (votingOpt.isEmpty()) {
            logger.warn("Voting not found with id: {}", id);
            return Optional.empty();
        }
        Voting voting = votingOpt.get();
        voting.setStatus(VotingStatus.CANCELLED);
        return Optional.of(votingRepository.save(voting).toShortDTO());
    }
}
