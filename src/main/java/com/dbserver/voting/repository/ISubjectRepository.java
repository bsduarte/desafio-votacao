package com.dbserver.voting.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dbserver.voting.model.Subject;
import com.dbserver.voting.model.SubjectVotingDTO;

public interface ISubjectRepository extends JpaRepository<Subject, UUID> {
    @Query("SELECT new com.dbserver.voting.model.SubjectVotingDTO(s.id, s.headline, s.description, v) " +
           "FROM Subject s LEFT JOIN Voting v ON s.id = v.subject.id WHERE s.id = :id")
    List<SubjectVotingDTO> findByIdWithVoting(@Param("id") UUID id); // Method to fetch all subjects with their voting details
}
