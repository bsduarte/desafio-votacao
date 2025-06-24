package com.dbserver.voting.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import com.dbserver.voting.descriptors.AssociatedDTODescriptor;
import com.dbserver.voting.dto.AssociatedDTO;
import com.dbserver.voting.service.IAssociatedService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

import static org.hamcrest.Matchers.equalTo;

@WebMvcTest(AssociatedController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
public class AssociatedControllerTest extends ControllerTest {

    @SuppressWarnings("removal")
    @MockBean
    private IAssociatedService associatedService;

    @Test
    @DisplayName("Should return 201 for POST /associated")
    void shouldReturn201ForAssociatedCreation() throws Exception {
        var associatedDTO = new AssociatedDTO(UUID.randomUUID(), "AAA" , "aaa@gmail.com", "1111111111111", null);

        when(associatedService.registerAssociated(any(AssociatedDTO.class))).thenReturn(associatedDTO);

        mockMvc.perform(
                    post("/associated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(associatedDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.email").isNotEmpty())
                .andExpect(jsonPath("$.phone").isNotEmpty())
                .andDo(document(
                    "associated-create",
                    requestFields(AssociatedDTODescriptor.getRequestFieldsDescriptor()),
                    responseFields(AssociatedDTODescriptor.getResponseFieldsDescriptor())));
    }

    @Test
    @DisplayName("Should return 200 for GET /associated/{id}")
    void shouldReturn200ForGetAssociatedById() throws Exception {
        var associatedDTO = new AssociatedDTO(UUID.randomUUID(), "AAA" , "aaa@gmail.com", "1111111111111", null);
        when(associatedService.registerAssociated(any(AssociatedDTO.class))).thenReturn(associatedDTO);
        mockMvc.perform(
                    post("/associated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(associatedDTO)))
                .andExpect(status().isCreated());
        when(associatedService.getAssociatedById(eq(associatedDTO.id()))).thenReturn(Optional.of(associatedDTO));

        mockMvc.perform(
                    get("/associated/{id}", associatedDTO.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(associatedDTO.id().toString()))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.email").isNotEmpty())
                .andExpect(jsonPath("$.phone").isNotEmpty())
                .andDo(document(
                    "associated-get",
                    pathParameters(
                        parameterWithName("id").description("The ID of the Associated")),
                    responseFields(AssociatedDTODescriptor.getResponseFieldsDescriptor())));
    }

    @Test
    @DisplayName("Should return 200 for GET /associated")
    void shouldReturn200ForGetAllAssociated() throws Exception {
        var associatedList = new ArrayList<AssociatedDTO>();
        var associatedCount = 3;
        for (int i = 0; i  < associatedCount; i++) {
            var associatedDTO = new AssociatedDTO(UUID.randomUUID(), "Associated" + i , i + "email@gmail.com", "111111111111" + i, null);
            associatedList.add(associatedDTO);
            when(associatedService.registerAssociated(any(AssociatedDTO.class))).thenReturn(associatedDTO);
            mockMvc.perform(
                    post("/associated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(associatedDTO)))
                .andExpect(status().isCreated());
        }
        when(associatedService.getAllAssociated()).thenReturn(associatedList);

        mockMvc.perform(
                    get("/associated")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(equalTo(associatedList.size())))
                .andDo(document("associated-list"));
    }

    @Test
    @DisplayName("Should return 200 for PUT /associated/{id}")
    void shouldReturn200ForUpdateAssociated() throws Exception {
        var associatedDTO1 = new AssociatedDTO(UUID.randomUUID(), "AAA" , "aaa@gmail.com", "1111111111111", null);
        var associatedDTO2 = new AssociatedDTO(associatedDTO1.id(), "BBB", "aaa@gmail.com", "1111111111111", null);

        when(associatedService.registerAssociated(any(AssociatedDTO.class))).thenReturn(associatedDTO1);

        mockMvc.perform(
                    post("/associated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(associatedDTO1)))
                .andExpect(status().isCreated());
        when(associatedService.updateAssociated(eq(associatedDTO1.id()), any(AssociatedDTO.class))).thenReturn(Optional.of(associatedDTO2));

        mockMvc.perform(
                    put("/associated/{id}", associatedDTO1.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(associatedDTO2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(associatedDTO1.id().toString()))
                .andExpect(jsonPath("$.name").value(associatedDTO2.name().toString()))
                .andDo(document(
                    "associated-update",
                    pathParameters(
                        parameterWithName("id").description("The Associated's ID")),
                    requestFields(AssociatedDTODescriptor.getRequestFieldsDescriptor()),    
                    responseFields(AssociatedDTODescriptor.getResponseFieldsDescriptor())));
    }

    @Test
    @DisplayName("Should return 204 for DELETE /associated/{id}")
    void shouldReturn204ForDeleteAssociated() throws Exception {
        var associatedDTO = new AssociatedDTO(UUID.randomUUID(), "AAA" , "aaa@gmail.com", "1111111111111", null);
        when(associatedService.registerAssociated(any(AssociatedDTO.class))).thenReturn(associatedDTO);
        mockMvc.perform(
                    post("/associated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(associatedDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(
                    delete("/associated/{id}", associatedDTO.id()))
                .andExpect(status().isNoContent())
                .andDo(document(
                    "associated-delete",
                    pathParameters(
                        parameterWithName("id").description("The Associated's ID"))));

        mockMvc.perform(
                    get("/associated/{id}", associatedDTO.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
