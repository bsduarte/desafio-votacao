package com.dbserver.voting.dto;

import java.util.UUID;

import com.dbserver.voting.model.Associated;
import com.dbserver.voting.model.Vote;
import com.dbserver.voting.model.Voting;

import jakarta.validation.constraints.NotNull;

public record ShortVoteDTO(UUID id,
                        @NotNull(message = "Vote voting is required")
                        UUID voting,
                        UUID associated,
                        @NotNull(message = "Vote value is required")
                        Boolean voteValue) {
    public Vote toEntity() {
        return Vote.builder()
                    .id(id)
                    .voting(Voting.builder().id(voting).build())
                    .associated(Associated.builder().id(associated).build())
                    .voteValue(voteValue)
                    .build();
    }
}

