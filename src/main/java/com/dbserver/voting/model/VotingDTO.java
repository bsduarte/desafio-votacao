package com.dbserver.voting.model;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

public record VotingDTO(UUID id,
                        Subject subject,
                        Duration votingInterval,
                        OffsetDateTime openedIn,
                        OffsetDateTime closesIn,
                        VotingStatus status,
                        VotingResult result,
                        Integer votesInFavor,
                        Integer votesAgainst) {
    public Voting toEntity() {
        return new Voting(id, subject, votingInterval, openedIn, closesIn, status, result, votesInFavor, votesAgainst);
    }

    public Voting toNewEntity() {
        return new Voting(null, subject, votingInterval, openedIn, closesIn, status, result, votesInFavor, votesAgainst);
    }
}
