package com.dbserver.voting.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dbserver.voting.dto.AssociatedDTO;
import com.dbserver.voting.model.Associated;
import com.dbserver.voting.repository.IAssociatedRepository;

@Service
public class AssociatedService implements IAssociatedService {
    
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
    public AssociatedDTO updateAssociated(UUID id, AssociatedDTO associatedDTO) {
        if (!associatedRepository.existsById(id)) {
            throw new RuntimeException("Associated not found with id: " + id);
        }
        Associated associated = associatedDTO.toEntity();
        associated.setId(id);
        return associatedRepository.save(associated).toDTO();
    }

    @Override
    public void deleteAssociated(UUID id) {
        associatedRepository.softDeleteById(id);
    }
}
