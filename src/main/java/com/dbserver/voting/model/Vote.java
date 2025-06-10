package com.dbserver.voting.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.GenerationType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voting", nullable = false)
    private Voting voting;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "associated", nullable = true)
    private Associated associated;

    private Boolean value;

    public VoteDTO toDTO() {
        return new VoteDTO(id, voting, associated, value);
    }
}
