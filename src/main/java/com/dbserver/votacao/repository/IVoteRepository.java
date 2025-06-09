package com.dbserver.votacao.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbserver.votacao.model.Vote;
import com.dbserver.votacao.model.Voting;

public interface IVoteRepository extends JpaRepository<Vote, UUID> {
    List<Vote> findByVoting(Voting voting);
}
