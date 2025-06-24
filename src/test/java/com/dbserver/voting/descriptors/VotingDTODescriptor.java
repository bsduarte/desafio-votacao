package com.dbserver.voting.descriptors;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;

public class VotingDTODescriptor {

    public static List<FieldDescriptor> getRequestFieldsDescriptor() {
        return List.of(
            fieldWithPath("id").ignored(),
            fieldWithPath("subject").description("Referred Subject"),
                fieldWithPath("subject.id").description("Referred Subject Id"),
                fieldWithPath("subject.headline").ignored(),
                fieldWithPath("subject.description").ignored(),
                fieldWithPath("subject.assemblies").ignored(),
            fieldWithPath("votingInterval").description("Voting Interval"),
            fieldWithPath("openedIn").ignored(),
            fieldWithPath("closesIn").ignored(),
            fieldWithPath("status").ignored(),
            fieldWithPath("result").ignored(),
            fieldWithPath("votesInFavor").ignored(),
            fieldWithPath("votesAgainst").ignored()
        );
    }

    public static List<FieldDescriptor> getResponseFieldsDescriptor() {
        return List.of(
            fieldWithPath("id").description("Voting ID"),
            fieldWithPath("subject").description("Referred Subject"),
                fieldWithPath("subject.id").description("Referred Subject Id"),
                fieldWithPath("subject.headline").description("Referred Subject headline"),
                fieldWithPath("subject.description").description("Referred Subject description"),
                fieldWithPath("subject.assemblies").description("Assemblies where referred subject were discussed"),
            fieldWithPath("votingInterval").description("Voting Interval"),
            fieldWithPath("openedIn").description("When Voting was started"),
            fieldWithPath("closesIn").description("When Voting closes"),
            fieldWithPath("status").description("The Voting Status"),
            fieldWithPath("result").description("The Voting Result"),
            fieldWithPath("votesInFavor").description("Count of Votes in favor"),
            fieldWithPath("votesAgainst").description("Count of Votes against")
        );
    }
}
