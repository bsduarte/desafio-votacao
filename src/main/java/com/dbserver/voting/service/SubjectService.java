package com.dbserver.voting.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dbserver.voting.dto.ShortSubjectDTO;
import com.dbserver.voting.dto.SubjectDTO;
import com.dbserver.voting.dto.SubjectResultsDTO;
import com.dbserver.voting.model.Subject;
import com.dbserver.voting.model.SubjectAssembly;
import com.dbserver.voting.repository.ISubjectAssemblyRepository;
import com.dbserver.voting.repository.ISubjectRepository;

@Service
public class SubjectService implements ISubjectService {

    private static final Logger logger = LoggerFactory.getLogger(SubjectService.class);

    private final ISubjectRepository subjectRepository;
    private final ISubjectAssemblyRepository subjectAssemblyRepository;

    public SubjectService(ISubjectRepository subjectRepository, ISubjectAssemblyRepository subjectAssemblyRepository) {
        this.subjectRepository = subjectRepository;
        this.subjectAssemblyRepository = subjectAssemblyRepository;
    }

    @Override
    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(Subject::toDTO)
                .toList();
    }

    @Override
    public Optional<SubjectDTO> getSubjectById(UUID id) {
        Optional<Subject> optSubject = subjectRepository.findById(id);
        if (optSubject.isEmpty()) {
            return Optional.empty();
        }

        Subject subject = optSubject.get();
        List<SubjectAssembly> subjectAssemblies = subjectAssemblyRepository.findBySubject(subject);
        return Optional.of(new SubjectDTO(
                subject.getId(),
                subject.getHeadline(),
                subject.getDescription(),
                subjectAssemblies.stream().map(subjectAssembly -> subjectAssembly.getAssembly().toDTO()).toList()));
    }

    @Override
    public Optional<SubjectResultsDTO> getSubjectResultsById(UUID id) {
        return Optional.ofNullable(SubjectResultsDTO.fromSubjectVotingList(subjectRepository.findByIdWithVoting(id)));
    }

    @Override
    public ShortSubjectDTO registerSubject(ShortSubjectDTO shortSubjectDTO) {
        Subject subject = shortSubjectDTO.toEntity();
        Subject savedSubject = subjectRepository.save(subject);
        return savedSubject.toShortDTO();
    }

    @Override
    public Optional<ShortSubjectDTO> updateSubject(UUID id, ShortSubjectDTO shortSubjectDTO) {
        if (!subjectRepository.existsById(id)) {
            logger.warn("Subject not found with id: {}", id);
            return Optional.empty();
        }
        Subject subject = shortSubjectDTO.toEntity();
        subject.setId(id);
        return Optional.of( subjectRepository.save(subject).toShortDTO());
    }

    @Override
    public void deleteSubject(UUID id) {
        subjectRepository.deleteById(id);
    }
}
