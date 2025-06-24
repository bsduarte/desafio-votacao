package com.dbserver.voting.descriptors;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;

public class ShortVoteDTODescriptor {

    public static List<FieldDescriptor> getRequestFieldsDescriptor() {
        return List.of(
            fieldWithPath("id").ignored(),
            fieldWithPath("voting").description("Referred Voting ID"),
            fieldWithPath("associated").description("Referred Associated ID"),
            fieldWithPath("voteValue").description("Vote value")
        );
    }

    public static List<FieldDescriptor> getResponseFieldsDescriptor() {
        return List.of(
            fieldWithPath("id").ignored(),
            fieldWithPath("voting").description("Referred Voting ID"),
            fieldWithPath("associated").ignored(),
            fieldWithPath("voteValue").description("Vote value")
        );
    }
}
