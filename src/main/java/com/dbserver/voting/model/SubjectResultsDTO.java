package com.dbserver.voting.model;

import java.util.List;
import java.util.UUID;

public record SubjectResultsDTO(UUID id, String headline, String description, List<VotingDTO> voting) {
    public static SubjectResultsDTO fromSubjectVotingList(List<SubjectVotingDTO> subjectVotingList) {
        if (subjectVotingList.isEmpty()) {
            return null;
        }
        SubjectVotingDTO subject = subjectVotingList.get(0);
        List<VotingDTO> votingList = subjectVotingList.stream()
                .map(subjectVoting -> subjectVoting.voting().toDTO())
                .toList();
        return new SubjectResultsDTO(subject.id(), subject.headline(), subject.description(), votingList);
    }
}
