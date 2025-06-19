package com.dbserver.voting.dto;

import java.util.List;
import java.util.UUID;

public record SubjectResultsDTO(UUID id,
                                String headline,
                                String description, 
                                List<ShortVotingDTO> voting) {
    public static SubjectResultsDTO fromSubjectVotingList(List<SubjectVotingDTO> subjectVotingList) {
        if (subjectVotingList.isEmpty()) {
            return null;
        }
        SubjectVotingDTO subject = subjectVotingList.get(0);
        List<ShortVotingDTO> votingList = subjectVotingList.stream()
                .map(subjectVoting -> subjectVoting.voting().toShortDTO())
                .toList();
        return new SubjectResultsDTO(subject.id(), subject.headline(), subject.description(), votingList);
    }
}
