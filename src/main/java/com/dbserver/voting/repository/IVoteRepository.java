package com.dbserver.voting.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbserver.voting.model.Vote;
import com.dbserver.voting.model.Voting;

public interface IVoteRepository extends JpaRepository<Vote, UUID> {
    List<Vote> findByVoting(Voting voting);
}
