package com.dbserver.voting.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dbserver.voting.domain.SubjectAssemblyId;
import com.dbserver.voting.dto.ShortSubjectAssemblyDTO;
import com.dbserver.voting.dto.SubjectAssemblyDTO;
import com.dbserver.voting.model.SubjectAssembly;
import com.dbserver.voting.repository.ISubjectAssemblyRepository;

import jakarta.ws.rs.NotFoundException;

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
        if (!subjectAssemblyRepository.existsById(SubjectAssemblyId.of(shortSubjectAssemblyDTO.subject(), shortSubjectAssemblyDTO.assembly()))) {
            throw new NotFoundException(
                "Subject/Assembly not found with subject: " + shortSubjectAssemblyDTO.subject() 
                + " and assembly: " + shortSubjectAssemblyDTO.assembly());
        }
        subjectAssemblyRepository.delete(shortSubjectAssemblyDTO.toEntity());
    }
}
