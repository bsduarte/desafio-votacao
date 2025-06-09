package com.dbserver.votacao.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbserver.votacao.model.Subject;
import com.dbserver.votacao.model.Voting;
import com.dbserver.votacao.model.VotingStatus;

public interface IVotingRepository extends JpaRepository<Voting, UUID> {
    List<Voting> findByStatus(VotingStatus status);
    List<Voting> findBySubject(Subject subjectId);
}
