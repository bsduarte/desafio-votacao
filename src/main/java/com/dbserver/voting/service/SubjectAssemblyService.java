package com.dbserver.voting.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dbserver.voting.dto.ShortSubjectAssemblyDTO;
import com.dbserver.voting.dto.SubjectAssemblyDTO;
import com.dbserver.voting.model.SubjectAssembly;
import com.dbserver.voting.repository.ISubjectAssemblyRepository;

@Service
public class SubjectAssemblyService implements ISubjectAssemblyService {

    private final ISubjectAssemblyRepository subjectAssemblyRepository;

    public SubjectAssemblyService(ISubjectAssemblyRepository subjectAssemblyRepository) {
        this.subjectAssemblyRepository = subjectAssemblyRepository;
    }

    @Override
    public List<SubjectAssemblyDTO> getAllSubjectAssemblies() {
        List<SubjectAssembly> subjectAssemblies = subjectAssemblyRepository.findAll();
        return subjectAssemblies.stream()
                .map(SubjectAssembly::toDTO)
                .toList();
    }

    @Override
    public ShortSubjectAssemblyDTO registerSubjectAssembly(ShortSubjectAssemblyDTO shortSubjectAssemblyDTO) {
        SubjectAssembly subjectAssembly = shortSubjectAssemblyDTO.toEntity();
        SubjectAssembly savedSubjectAssembly = subjectAssemblyRepository.save(subjectAssembly);
        return savedSubjectAssembly.toShortDTO();
    }

    @Override
    public void deleteSubjectAssembly(ShortSubjectAssemblyDTO shortSubjectAssemblyDTO) {
        SubjectAssembly subjectAssembly = shortSubjectAssemblyDTO.toEntity();
        subjectAssemblyRepository.delete(subjectAssembly);
    }
}
