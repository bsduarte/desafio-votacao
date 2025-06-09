package com.dbserver.votacao.model;

import java.util.UUID;

public record VoteDTO(UUID id, Voting voting, Associated associated, Boolean vote) {
    public Vote toEntity() {
        return new Vote(id, voting, associated, vote);
    }
}
