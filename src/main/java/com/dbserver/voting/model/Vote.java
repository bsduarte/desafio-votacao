package com.dbserver.voting.model;

import java.util.UUID;

import com.dbserver.voting.dto.ShortVoteDTO;
import com.dbserver.voting.dto.VoteDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.GenerationType;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "voting", nullable = false, updatable = false)
    @JsonBackReference
    private Voting voting;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "associated", nullable = true, updatable = false)
    @JsonBackReference
    private Associated associated;

    @Column(updatable = false)
    private Boolean voteValue;

    public VoteDTO toDTO() {
        return new VoteDTO(id, voting.toDTO(), associated.toDTO(), voteValue);
    }

    public ShortVoteDTO toShortDTO() {
        return new ShortVoteDTO(id, voting.getId(), associated.getId(), voteValue);
    }
}
