package com.dbserver.voting.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dbserver.voting.dto.AssemblyDTO;
import com.dbserver.voting.model.Assembly;
import com.dbserver.voting.repository.IAssemblyRepository;

@Service
public class AssemblyService implements IAssemblyService {

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
    public AssemblyDTO updateAssembly(UUID id, AssemblyDTO assemblyDTO) {
        if (!assemblyRepository.existsById(id)) {
            throw new RuntimeException("Assembly not found with id: " + id);
        }
        Assembly assembly = assemblyDTO.toEntity();
        assembly.setId(id);
        return assemblyRepository.save(assembly).toDTO();
    }

    @Override
    public void deleteAssembly(UUID id) {
        assemblyRepository.deleteById(id);
    }
}
