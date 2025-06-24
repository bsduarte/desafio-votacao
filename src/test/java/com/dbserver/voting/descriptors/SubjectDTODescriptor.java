package com.dbserver.voting.descriptors;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;

public class SubjectDTODescriptor {

    public static List<FieldDescriptor> getRequestFieldsDescriptor() {
        return List.of(
            fieldWithPath("id").ignored(),
            fieldWithPath("headline").description("The Subject headline"),
            fieldWithPath("description").description("The Subject description"),
            fieldWithPath("assemblies").ignored()
        );
    }

    public static List<FieldDescriptor> getResponseFieldsDescriptor() {
        return List.of(
            fieldWithPath("id").description("The Subject Id"),
            fieldWithPath("headline").description("The Subject headline"),
            fieldWithPath("description").description("The Subject description"),
            fieldWithPath("assemblies").description("The list of Assemblies where the Subject was discussed")
        );
    } 
}
