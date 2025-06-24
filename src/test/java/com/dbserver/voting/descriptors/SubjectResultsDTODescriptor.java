package com.dbserver.voting.descriptors;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;

public class SubjectResultsDTODescriptor {

    public static List<FieldDescriptor> getResponseFieldsDescriptor() {
        return List.of(
            fieldWithPath("id").description("The Subject Id"),
            fieldWithPath("headline").description("The Subject headline"),
            fieldWithPath("description").description("The Subject description"),
            fieldWithPath("voting").description("The voting list of the Subject"),
                fieldWithPath("voting[].id").description("Voting ID"),
                fieldWithPath("voting[].subject").description("The Subject ID"),
                fieldWithPath("voting[].votingInterval").description("Voting Interval"),
                fieldWithPath("voting[].openedIn").description("When Voting was started"),
                fieldWithPath("voting[].closesIn").description("When Voting closes"),
                fieldWithPath("voting[].status").description("The Voting Status"),
                fieldWithPath("voting[].result").description("The Voting Result"),
                fieldWithPath("voting[].votesInFavor").description("Count of Votes in favor"),
                fieldWithPath("voting[].votesAgainst").description("Count of Votes against")
        );
    } 
}
