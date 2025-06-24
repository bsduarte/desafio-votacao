package com.dbserver.voting.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dbserver.voting.dto.AssociatedDTO;
import com.dbserver.voting.model.Associated;
import com.dbserver.voting.repository.IAssociatedRepository;

import jakarta.ws.rs.NotFoundException;

@Service
public class AssociatedService implements IAssociatedService {

    private static final Logger logger = LoggerFactory.getLogger(AssociatedService.class);

    private final IAssociatedRepository associatedRepository;

    public AssociatedService(IAssociatedRepository associatedRepository) {
        this.associatedRepository = associatedRepository;
    }

    @Override
    public List <AssociatedDTO> getAllAssociated() {
        return associatedRepository.findAll().stream()
                .map(Associated::toDTO)
                .toList();
    }

    @Override
    public Optional<AssociatedDTO> getAssociatedById(UUID id) {
        return associatedRepository.findById(id)
                .map(Associated::toDTO);
    }

    @Override
    public AssociatedDTO registerAssociated(AssociatedDTO associatedDTO) {
        Associated associated = associatedDTO.toEntity();
        Associated savedAssociated = associatedRepository.save(associated);
        return savedAssociated.toDTO();
    }

    @Override
    public Optional<AssociatedDTO> updateAssociated(UUID id, AssociatedDTO associatedDTO) {
        if (!associatedRepository.existsById(id)) {
            logger.warn("Associated not found with id: {}", id);
            return Optional.empty();
        }
        Associated associated = associatedDTO.toEntity();
        associated.setId(id);
        return Optional.of(associatedRepository.save(associated).toDTO());
    }

    @Override
    public void deleteAssociated(UUID id) {
        if (!associatedRepository.existsById(id)) {
            throw new NotFoundException("Associated not found with id: " + id);
        }        
        associatedRepository.softDeleteById(id);
    }
}
