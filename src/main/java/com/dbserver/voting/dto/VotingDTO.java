package com.dbserver.voting.dto;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.dbserver.voting.domain.VotingResult;
import com.dbserver.voting.domain.VotingStatus;
import com.dbserver.voting.model.Voting;

import jakarta.validation.constraints.NotNull;

public record VotingDTO(UUID id,
                       @NotNull(message = "Voting subject is required")
                       SubjectDTO subject,
                       Duration votingInterval,
                       OffsetDateTime openedIn,
                       OffsetDateTime closesIn,
                       VotingStatus status,
                       VotingResult result,
                       Integer votesInFavor,
                       Integer votesAgainst) {

    public Voting toEntity() {
        return Voting.builder()
                .id(id)
                .subject(subject.toEntity())
                .votingInterval(votingInterval)
                .openedIn(openedIn)
                .closesIn(closesIn)
                .status(status)
                .result(result)
                .votesInFavor(votesInFavor)
                .votesAgainst(votesAgainst)
                .build();
    }

    public ShortVotingDTO toShortDTO() {
        return new ShortVotingDTO(id,
                                subject.id(),
                                votingInterval,
                                openedIn,
                                closesIn,
                                status,
                                result,
                                votesInFavor,
                                votesAgainst);
    }
}
