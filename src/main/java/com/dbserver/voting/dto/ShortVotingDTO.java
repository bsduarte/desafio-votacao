package com.dbserver.voting.dto;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.dbserver.voting.domain.VotingResult;
import com.dbserver.voting.domain.VotingStatus;
import com.dbserver.voting.model.Subject;
import com.dbserver.voting.model.Voting;

import jakarta.validation.constraints.NotNull;

public record ShortVotingDTO(UUID id,
                            @NotNull(message = "Voting subject is required")
                            UUID subject,
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
                .subject(Subject.builder().id(subject).build())
                .votingInterval(votingInterval)
                .openedIn(openedIn)
                .closesIn(closesIn)
                .status(status)
                .result(result)
                .votesInFavor(votesInFavor)
                .votesAgainst(votesAgainst)
                .build();
    }

    public VotingDTO toDTO() {
        return new VotingDTO(id,
                            new SubjectDTO(subject, null, null, null),
                            votingInterval,
                            openedIn,
                            closesIn,
                            status,
                            result,
                            votesInFavor,
                            votesAgainst);
    }
}
