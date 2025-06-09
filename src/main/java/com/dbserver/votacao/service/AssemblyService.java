package com.dbserver.votacao.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.dbserver.votacao.model.Assembly;
import com.dbserver.votacao.model.AssemblyDTO;
import com.dbserver.votacao.repository.IAssemblyRepository;

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
        Assembly assembly = assemblyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assembly not found with id: " + id));
        assembly.setDay(assemblyDTO.day());
        Assembly updatedAssembly = assemblyRepository.save(assembly);
        return updatedAssembly.toDTO();
    }

    @Override
    public void deleteAssembly(UUID id) {
        assemblyRepository.deleteById(id);
    }
}
