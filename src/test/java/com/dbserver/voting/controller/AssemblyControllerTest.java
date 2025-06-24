package com.dbserver.voting.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import com.dbserver.voting.descriptors.AssemblyDTODescriptor;
import com.dbserver.voting.dto.AssemblyDTO;
import com.dbserver.voting.service.IAssemblyService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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

@WebMvcTest(AssemblyController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
public class AssemblyControllerTest extends ControllerTest {

    @SuppressWarnings("removal")
    @MockBean
    private IAssemblyService assemblyService;

    @Test
    @DisplayName("Should return 201 for POST /assembly")
    void shouldReturn201ForAssemblyCreation() throws Exception {
        var assemblyDTO = new AssemblyDTO(UUID.randomUUID(), LocalDate.now());

        when(assemblyService.registerAssembly(any(AssemblyDTO.class))).thenReturn(assemblyDTO);

        mockMvc.perform(
                    post("/assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assemblyDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.assemblyDate").isNotEmpty())
                .andDo(document(
                    "assembly-create",
                    requestFields(AssemblyDTODescriptor.getRequestFieldsDescriptor()),
                    responseFields(AssemblyDTODescriptor.getResponseFieldsDescriptor())));
    }

    @Test
    @DisplayName("Should return 200 for GET /assembly/{id}")
    void shouldReturn200ForGetAssemblyById() throws Exception {
        var assemblyDTO = new AssemblyDTO(UUID.randomUUID(), LocalDate.now());
        when(assemblyService.registerAssembly(any(AssemblyDTO.class))).thenReturn(assemblyDTO);

        mockMvc.perform(
                    post("/assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assemblyDTO)))
                .andExpect(status().isCreated());
        when(assemblyService.getAssemblyById(eq(assemblyDTO.id()))).thenReturn(Optional.of(assemblyDTO));

        mockMvc.perform(
                    get("/assembly/{id}", assemblyDTO.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(assemblyDTO.id().toString()))
                .andExpect(jsonPath("$.assemblyDate").isNotEmpty())
                .andDo(document(
                    "assembly-get",
                    pathParameters(
                        parameterWithName("id").description("The ID of the Assembly")),
                    responseFields(AssemblyDTODescriptor.getResponseFieldsDescriptor())));
    }

    @Test
    @DisplayName("Should return 200 for GET /assembly")
    void shouldReturn200ForGetAllAssemblies() throws Exception {
        var assemblyList = new ArrayList<AssemblyDTO>();
        var assembliesCount = 3;
        for (int i = 0; i < assembliesCount; i++) {
            var assemblyDTO = new AssemblyDTO(UUID.randomUUID(), LocalDate.now().plusDays(i));
            assemblyList.add(assemblyDTO);
            when(assemblyService.registerAssembly(any(AssemblyDTO.class))).thenReturn(assemblyDTO);
            mockMvc.perform(
                    post("/assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assemblyDTO)))
                .andExpect(status().isCreated());
        }
        when(assemblyService.getAllAssemblies()).thenReturn(assemblyList);

        mockMvc.perform(
                    get("/assembly")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(equalTo(assembliesCount)))
                .andDo(document("assemblies-list"));
    }

    @Test
    @DisplayName("Should return 200 for PUT /assembly/{id}")
    void shouldReturn200ForUpdateAssembly() throws Exception {
        var assemblyDTO1 = new AssemblyDTO(UUID.randomUUID(), LocalDate.now());
        var assemblyDTO2 = new AssemblyDTO(assemblyDTO1.id(), assemblyDTO1.assemblyDate().plusDays(1));

        when(assemblyService.registerAssembly(any(AssemblyDTO.class))).thenReturn(assemblyDTO1);

        mockMvc.perform(
                    post("/assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assemblyDTO1)))
                .andExpect(status().isCreated());

        when(assemblyService.updateAssembly(eq(assemblyDTO1.id()), any(AssemblyDTO.class))).thenReturn(Optional.of(assemblyDTO2));

        mockMvc.perform(
                    put("/assembly/{id}", assemblyDTO1.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assemblyDTO2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(assemblyDTO1.id().toString()))
                .andExpect(jsonPath("$.assemblyDate").value(assemblyDTO2.assemblyDate().toString()))
                .andDo(document(
                    "assembly-update",
                    pathParameters(
                        parameterWithName("id").description("The ID of the Assembly")),
                    requestFields(AssemblyDTODescriptor.getRequestFieldsDescriptor()),    
                    responseFields(AssemblyDTODescriptor.getResponseFieldsDescriptor())));
    }

    @Test
    @DisplayName("Should return 204 for DELETE /assembly/{id}")
    void shouldReturn204ForDeleteAssembly() throws Exception {
        var assemblyDTO = new AssemblyDTO(UUID.randomUUID(), LocalDate.now());
        when(assemblyService.registerAssembly(any(AssemblyDTO.class))).thenReturn(assemblyDTO);
        mockMvc.perform(
                    post("/assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assemblyDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(
                    delete("/assembly/{id}", assemblyDTO.id()))
                .andExpect(status().isNoContent())
                .andDo(document(
                    "assembly-delete",
                    pathParameters(
                        parameterWithName("id").description("The ID of the Assembly"))));

        mockMvc.perform(
                    get("/assembly/{id}", assemblyDTO.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
