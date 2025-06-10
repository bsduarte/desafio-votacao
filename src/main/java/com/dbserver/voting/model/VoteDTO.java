package com.dbserver.voting.model;

import java.util.UUID;

public record VoteDTO(UUID id, Voting voting, Associated associated, Boolean value) {
    public Vote toEntity() {
        return new Vote(id, voting, associated, value);
    }

    public Vote toNewEntity() {
        return new Vote(null, voting, associated, value);
    }
}
