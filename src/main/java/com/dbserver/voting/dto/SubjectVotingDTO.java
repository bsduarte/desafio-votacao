package com.dbserver.voting.dto;

import java.util.UUID;

import com.dbserver.voting.model.Voting;

public record SubjectVotingDTO(UUID id, String headline, String description, Voting voting) {    
}
