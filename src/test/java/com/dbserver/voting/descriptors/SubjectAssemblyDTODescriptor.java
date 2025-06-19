package com.dbserver.voting.descriptors;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;

public class SubjectAssemblyDTODescriptor {

    public static List<FieldDescriptor> getRequestFieldsDescriptor() {
        return List.of(
            fieldWithPath("subject").description("Referred Subject"),
                fieldWithPath("subject.id").description("Referred Subject Id"),
                fieldWithPath("subject.headline").ignored(),
                fieldWithPath("subject.description").ignored(),
                fieldWithPath("subject.assemblies").ignored(),
            fieldWithPath("assembly").description("Referred Assembly"),
                fieldWithPath("assembly.id").description("Referred Assembly Id"),
                fieldWithPath("assembly.assemblyDate").ignored()
        );
    }

    public static List<FieldDescriptor> getResponseFieldsDescriptor() {
        return List.of(
            fieldWithPath("subject").description("Referred Subject"),
                fieldWithPath("subject.id").description("Referred Subject Id"),
                fieldWithPath("subject.headline").description("Referred Subject headline"),
                fieldWithPath("subject.description").description("Referred Subject description"),
                fieldWithPath("subject.assemblies").ignored(),
            fieldWithPath("assembly").description("Referred Assembly"),
                fieldWithPath("assembly.id").description("Referred Assembly Id"),
                fieldWithPath("assembly.assemblyDate").description("Referred Assembly Date")
        );
    }
}
