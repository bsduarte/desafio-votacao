package com.dbserver.voting.descriptors;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;

public class ShortSubjectAssemblyDTODescriptor {

    public static List<FieldDescriptor> getRequestFieldsDescriptor() {
        return List.of(
            fieldWithPath("subject").description("Referred Subject ID"),
            fieldWithPath("assembly").description("Referred Assembly ID")
        );
    }

    public static List<FieldDescriptor> getResponseFieldsDescriptor() {
        return List.of(
            fieldWithPath("subject").description("Referred Subject ID"),
            fieldWithPath("assembly").description("Referred Assembly ID")
        );
    }
}
