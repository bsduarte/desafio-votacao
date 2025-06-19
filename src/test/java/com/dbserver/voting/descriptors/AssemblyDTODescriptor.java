package com.dbserver.voting.descriptors;

import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;


public final class AssemblyDTODescriptor {

    public static List<FieldDescriptor> getRequestFieldsDescriptor() {
        return List.of(
            fieldWithPath("id").ignored(),
            fieldWithPath("assemblyDate").description("The Assembly Date")
        );
    }

    public static List<FieldDescriptor> getResponseFieldsDescriptor() {
            return List.of(
                fieldWithPath("id").description("The Assembly Id"),
                fieldWithPath("assemblyDate").description("The Assembly Date")
            );
    }    
}
