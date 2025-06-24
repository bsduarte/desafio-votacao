package com.dbserver.voting.descriptors;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;

public class VoteDTODescriptor {

    public static List<FieldDescriptor> getRequestFieldsDescriptor() {
        return List.of(
            fieldWithPath("id").ignored(),
            fieldWithPath("voting").description("Referred Voting"),
                fieldWithPath("voting.id").description("Voting ID"),
                fieldWithPath("voting.subject").ignored(),
                    fieldWithPath("voting.subject.id").ignored(),
                    fieldWithPath("voting.subject.headline").ignored(),
                    fieldWithPath("voting.subject.description").ignored(),
                    fieldWithPath("voting.subject.assemblies").ignored(),
                fieldWithPath("voting.votingInterval").ignored(),
                fieldWithPath("voting.openedIn").ignored(),
                fieldWithPath("voting.closesIn").ignored(),
                fieldWithPath("voting.status").ignored(),
                fieldWithPath("voting.result").ignored(),
                fieldWithPath("voting.votesInFavor").ignored(),
                fieldWithPath("voting.votesAgainst").ignored(),
            fieldWithPath("associated").description("Referred Associated"),
                fieldWithPath("associated.id").description("Associated ID"),
                fieldWithPath("associated.name").ignored(),
                fieldWithPath("associated.email").ignored(),
                fieldWithPath("associated.phone").ignored(),
                fieldWithPath("associated.active").ignored(),
            fieldWithPath("voteValue").description("Vote value")
        );
    }

    public static List<FieldDescriptor> getResponseFieldsDescriptor() {
        return List.of(
            fieldWithPath("id").ignored(),
            fieldWithPath("voting").description("Referred Voting"),
                fieldWithPath("voting.id").description("Voting ID"),
                fieldWithPath("voting.subject").description("Referred Subject"),
                    fieldWithPath("voting.subject.id").description("Referred Subject Id"),
                    fieldWithPath("voting.subject.headline").description("Referred Subject headline"),
                    fieldWithPath("voting.subject.description").description("Referred Subject description"),
                    fieldWithPath("voting.subject.assemblies").description("Assemblies where referred subject were discussed"),
                fieldWithPath("voting.votingInterval").description("Voting Interval"),
                fieldWithPath("voting.openedIn").description("When Voting was started"),
                fieldWithPath("voting.closesIn").description("When Voting closes"),
                fieldWithPath("voting.status").description("The Voting Status"),
                fieldWithPath("voting.result").description("The Voting Result"),
                fieldWithPath("voting.votesInFavor").description("Count of Votes in favor"),
                fieldWithPath("voting.votesAgainst").description("Count of Votes against"),
            fieldWithPath("associated").ignored(),
                fieldWithPath("associated.id").ignored(),
                fieldWithPath("associated.name").ignored(),
                fieldWithPath("associated.email").ignored(),
                fieldWithPath("associated.phone").ignored(),
                fieldWithPath("associated.active").ignored(),
            fieldWithPath("voteValue").description("Vote value")
        );
    }
}
