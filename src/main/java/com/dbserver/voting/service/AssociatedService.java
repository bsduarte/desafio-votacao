package com.dbserver.voting.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dbserver.voting.model.Associated;
import com.dbserver.voting.model.AssociatedDTO;
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
        Associated associated = associatedRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Associated not found with id: " + id));
        associated.setName(associatedDTO.name());
        associated.setEmail(associatedDTO.email());
        associated.setPhone(associatedDTO.phone());
        Associated updatedAssociated = associatedRepository.save(associated);
        return updatedAssociated.toDTO();
    }

    @Override
    public void deleteAssociated(UUID id) {
        associatedRepository.deleteById(id);
    }
}
