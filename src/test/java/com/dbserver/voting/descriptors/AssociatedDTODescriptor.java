package com.dbserver.voting.descriptors;

import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssociatedDTODescriptor {

    public static List<FieldDescriptor> getRequestFieldsDescriptor() {
        return List.of(
            fieldWithPath("id").ignored(),
            fieldWithPath("name").description("The Associated's Name"),
            fieldWithPath("email").description("The Associated's Email"),
            fieldWithPath("phone").description("The Associated's Phone"),
            fieldWithPath("active").description("Indicates if the Associated is Active")
        );
    }

    public static List<FieldDescriptor> getResponseFieldsDescriptor() {
        return List.of(
            fieldWithPath("id").description("The Associated's Id"),
            fieldWithPath("name").description("The Associated's Name"),
            fieldWithPath("email").description("The Associated's Email"),
            fieldWithPath("phone").description("The Associated's Phone"),
            fieldWithPath("active").description("Indicates if the Associated is Active")
        );
    }  
}
