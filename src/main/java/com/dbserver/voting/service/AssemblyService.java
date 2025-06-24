package com.dbserver.voting.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dbserver.voting.dto.AssemblyDTO;
import com.dbserver.voting.model.Assembly;
import com.dbserver.voting.repository.IAssemblyRepository;

import jakarta.ws.rs.NotFoundException;

@Service
public class AssemblyService implements IAssemblyService {

    private static final Logger logger = LoggerFactory.getLogger(AssemblyService.class);

    private final IAssemblyRepository assemblyRepository;

    public AssemblyService(IAssemblyRepository assemblyRepository) {
        this.assemblyRepository = assemblyRepository;
    }

    @Override
    public List<AssemblyDTO> getAllAssemblies() {
        return assemblyRepository.findAll().stream()
                .map(Assembly::toDTO)
                .toList();
    }

    @Override
    public Optional<AssemblyDTO> getAssemblyById(UUID id) {
        return assemblyRepository.findById(id)
                .map(Assembly::toDTO);
    }

    @Override
    public AssemblyDTO registerAssembly(AssemblyDTO assemblyDTO) {
        Assembly assembly = assemblyDTO.toEntity();
        Assembly savedAssembly = assemblyRepository.save(assembly);
        return savedAssembly.toDTO();
    }

    @Override
    public Optional<AssemblyDTO> updateAssembly(UUID id, AssemblyDTO assemblyDTO) {
        if (!assemblyRepository.existsById(id)) {
            logger.warn("Assembly not found with id: {} ", id);
            return Optional.empty();
        }
        Assembly assembly = assemblyDTO.toEntity();
        assembly.setId(id);
        return Optional.of(assemblyRepository.save(assembly).toDTO());
    }

    @Override
    public void deleteAssembly(UUID id) {
        if (!assemblyRepository.existsById(id)) {
            throw new NotFoundException("Assembly not found with id: " + id);
        }        
        assemblyRepository.deleteById(id);
    }
}
