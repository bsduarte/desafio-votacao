package com.dbserver.voting.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dbserver.voting.model.Subject;
import com.dbserver.voting.model.SubjectDTO;
import com.dbserver.voting.repository.ISubjectRepository;

@Service
public class SubjectService implements ISubjectService {
    
    private final ISubjectRepository subjectRepository;

    public SubjectService(ISubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }
    
    @Override
    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(Subject::toDTO)
                .toList();
    }

    @Override
    public Optional<SubjectDTO> getSubjectById(UUID id) {
        return subjectRepository.findById(id)
                .map(Subject::toDTO);
    }

    @Override
    public SubjectDTO registerSubject(SubjectDTO subjectDTO) {
        Subject subject = subjectDTO.toEntity();
        Subject savedSubject = subjectRepository.save(subject);
        return savedSubject.toDTO();
    }

    @Override
    public SubjectDTO updateSubject(UUID id, SubjectDTO subjectDTO) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));
        subject.setHeadline(subjectDTO.headline());
        subject.setDescription(subjectDTO.description());
        Subject updatedSubject = subjectRepository.save(subject);
        return updatedSubject.toDTO();
    }

    @Override
    public void deleteSubject(UUID id) {
        subjectRepository.deleteById(id);
    }
}
