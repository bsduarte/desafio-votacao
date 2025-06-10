package com.dbserver.voting.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbserver.voting.model.Subject;
import com.dbserver.voting.model.Voting;
import com.dbserver.voting.model.VotingStatus;

public interface IVotingRepository extends JpaRepository<Voting, UUID> {
    List<Voting> findByStatus(VotingStatus status);
    List<Voting> findBySubject(Subject subjectId);
}
