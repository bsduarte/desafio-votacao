package com.dbserver.voting.model;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import com.dbserver.voting.domain.VotingResult;
import com.dbserver.voting.domain.VotingStatus;
import com.dbserver.voting.dto.ShortVotingDTO;
import com.dbserver.voting.dto.VotingDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;

import io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Voting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "subject", nullable = false, updatable = false)
    @JsonBackReference
    private Subject subject;

    @Type(PostgreSQLIntervalType.class)
    @Column(
        name = "voting_interval",
        columnDefinition = "interval",
        nullable = false)
    private Duration votingInterval;

    @Column(insertable = false, updatable = false)
    private OffsetDateTime openedIn;
    @Column(insertable = false, updatable = false)
    private OffsetDateTime closesIn;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false, insertable = false)
    private VotingStatus status;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "result", nullable = true, insertable = false, updatable = false)
    private VotingResult result;

    @Column(insertable = false, updatable = false)
    private Integer votesInFavor;
    @Column(insertable = false, updatable = false)
    private Integer votesAgainst;

    public VotingDTO toDTO() {
        return new VotingDTO(id,
                            subject.toDTO(),
                            votingInterval,
                            openedIn,
                            closesIn,
                            status,
                            result,
                            votesInFavor,
                            votesAgainst);
    }

    public ShortVotingDTO toShortDTO() {
        return new ShortVotingDTO(id,
                                subject.getId(),
                                votingInterval,
                                openedIn,
                                closesIn,
                                status,
                                result,
                                votesInFavor,
                                votesAgainst);
    }
}
