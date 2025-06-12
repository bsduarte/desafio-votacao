package com.dbserver.voting.model;

import java.util.UUID;

public record SubjectVotingDTO(UUID id, String headline, String description, Voting voting) {    
}
