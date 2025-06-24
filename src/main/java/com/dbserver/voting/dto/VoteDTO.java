package com.dbserver.voting.dto;

import java.util.UUID;

import com.dbserver.voting.model.Vote;

import jakarta.validation.constraints.NotNull;

public record VoteDTO(UUID id,
                      @NotNull(message = "Vote voting is required")
                      VotingDTO voting,
                      AssociatedDTO associated,
                      @NotNull(message = "Vote value is required")
                      Boolean voteValue) {

    public Vote toEntity() {
        return Vote.builder()
                .id(id)
                .voting(voting != null ? voting.toEntity() : null)
                .associated(associated != null ? associated.toEntity() : null)
                .voteValue(voteValue)
                .build();
    }

    public ShortVoteDTO toShortDTO() {
        return new ShortVoteDTO(id, voting.id(), associated.id(), voteValue);
    }
}
