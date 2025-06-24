package com.dbserver.voting.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import com.dbserver.voting.descriptors.ShortSubjectAssemblyDTODescriptor;
import com.dbserver.voting.dto.AssemblyDTO;
import com.dbserver.voting.dto.ShortSubjectAssemblyDTO;
import com.dbserver.voting.dto.ShortSubjectDTO;
import com.dbserver.voting.dto.SubjectDTO;
import com.dbserver.voting.dto.SubjectAssemblyDTO;
import com.dbserver.voting.service.IAssemblyService;
import com.dbserver.voting.service.ISubjectAssemblyService;
import com.dbserver.voting.service.ISubjectService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

@WebMvcTest(controllers = {SubjectAssemblyController.class, AssemblyController.class, SubjectController.class})
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
public class SubjectAssemblyControllerTest extends ControllerTest {

    @SuppressWarnings("removal")
    @MockBean
    private ISubjectAssemblyService subjectAssemblyService;

    @SuppressWarnings("removal")
    @MockBean
    private IAssemblyService assemblyService;

    @SuppressWarnings("removal")
    @MockBean
    private ISubjectService subjectService;

    @Test
    @DisplayName("Should return 201 for POST /subject-assembly")
    void shouldReturn201ForSubjectAssemblyAssociation() throws Exception {
        var assemblyDTO = new AssemblyDTO(UUID.randomUUID(), LocalDate.now());
        var shortSubjectDTO = new ShortSubjectDTO(UUID.randomUUID(), "Headline" , "Description description description");

        when(assemblyService.registerAssembly(any(AssemblyDTO.class))).thenReturn(assemblyDTO);
        when(subjectService.registerSubject(any(ShortSubjectDTO.class))).thenReturn(shortSubjectDTO);

        mockMvc.perform(
                    post("/assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assemblyDTO)))
                    .andExpect(status().isCreated());
        mockMvc.perform(
                    post("/subject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortSubjectDTO)))
                    .andExpect(status().isCreated());

        var shortSubjectAssemblyDTO = new ShortSubjectAssemblyDTO(shortSubjectDTO.id(), assemblyDTO.id());

        when(subjectAssemblyService.registerSubjectAssembly(any(ShortSubjectAssemblyDTO.class))).thenReturn(shortSubjectAssemblyDTO);

        mockMvc.perform(
                    post("/subject-assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortSubjectAssemblyDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subject").value(shortSubjectAssemblyDTO.subject().toString()))
                .andExpect(jsonPath("$.assembly").value(shortSubjectAssemblyDTO.assembly().toString()))
                .andDo(document(
                    "subject-assembly-create",
                    requestFields(ShortSubjectAssemblyDTODescriptor.getRequestFieldsDescriptor()),
                    responseFields(ShortSubjectAssemblyDTODescriptor.getResponseFieldsDescriptor())));
    }

    @Test
    @DisplayName("Should return 200 for GET /subject-assembly")
    void shouldReturn200ForGetAllSubjectAssembly() throws Exception {
        var assemblyDTO = new AssemblyDTO(UUID.randomUUID(), LocalDate.now());
        var assemblyDTOId = new AssemblyDTO(assemblyDTO.id(), null);

        when(assemblyService.registerAssembly(any(AssemblyDTO.class))).thenReturn(assemblyDTO);

        mockMvc.perform(
                    post("/assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assemblyDTO)))
                    .andExpect(status().isCreated());

        var subjectAssemblyList = new ArrayList<SubjectAssemblyDTO>();
        var subjectCount = 3;
        for (int i = 0; i < subjectCount; i++) {
            var shortSubjectDTO = new ShortSubjectDTO(UUID.randomUUID(), "Headline" + i , "Description" + i);
            when(subjectService.registerSubject(any(ShortSubjectDTO.class))).thenReturn(shortSubjectDTO);
            mockMvc.perform(
                    post("/subject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortSubjectDTO)))
                    .andExpect(status().isCreated());

            var subjectAssembly = new SubjectAssemblyDTO(
                                        new SubjectDTO(shortSubjectDTO.id(), null, null, null),
                                        assemblyDTOId);
            var shortSubjectAssembly = subjectAssembly.toShortDTO();
            subjectAssemblyList.add(subjectAssembly);
            when(subjectAssemblyService.registerSubjectAssembly(any(ShortSubjectAssemblyDTO.class))).thenReturn(shortSubjectAssembly);
            mockMvc.perform(
                    post("/subject-assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortSubjectAssembly)))
                .andExpect(status().isCreated());
        }
        when(subjectAssemblyService.getAllSubjectAssemblies()).thenReturn(subjectAssemblyList);

        mockMvc.perform(
                    get("/subject-assembly")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(equalTo(subjectAssemblyList.size())))
                .andDo(document("subject-assembly-list"));
    }

    @Test
    @DisplayName("Should return 204 for DELETE /subject-assembly")
    void shouldReturn204ForSubjectAssemblyDisassociation() throws Exception {
        var assemblyDTO = new AssemblyDTO(UUID.randomUUID(), LocalDate.now());
        var shortSubjectDTO = new ShortSubjectDTO(UUID.randomUUID(), "Headline" , "Description description description");
        var shortSubjectAssemblyDTO = new ShortSubjectAssemblyDTO(shortSubjectDTO.id(), assemblyDTO.id());

        when(assemblyService.registerAssembly(any(AssemblyDTO.class))).thenReturn(assemblyDTO);
        when(subjectService.registerSubject(any(ShortSubjectDTO.class))).thenReturn(shortSubjectDTO);
        when(subjectAssemblyService.registerSubjectAssembly(any(ShortSubjectAssemblyDTO.class))).thenReturn(shortSubjectAssemblyDTO);

        mockMvc.perform(
                    post("/assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assemblyDTO)))
                    .andExpect(status().isCreated());
        mockMvc.perform(
                    post("/subject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortSubjectDTO)))
                    .andExpect(status().isCreated());
        mockMvc.perform(
                    post("/subject-assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortSubjectAssemblyDTO)))
                .andExpect(status().isCreated());

         mockMvc.perform(
                    delete("/subject-assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortSubjectAssemblyDTO)))
                .andExpect(status().isNoContent())
                .andDo(document(
                    "subject-assembly-delete",
                    requestFields(ShortSubjectAssemblyDTODescriptor.getRequestFieldsDescriptor())));

        mockMvc.perform(
                    get("/subject-assembly")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(equalTo(0)));
    }
}
