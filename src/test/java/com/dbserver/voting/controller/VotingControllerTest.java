package com.dbserver.voting.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import com.dbserver.voting.descriptors.ShortVotingDTODescriptor;
import com.dbserver.voting.descriptors.VotingDTODescriptor;
import com.dbserver.voting.domain.VotingStatus;
import com.dbserver.voting.dto.AssemblyDTO;
import com.dbserver.voting.dto.ShortSubjectAssemblyDTO;
import com.dbserver.voting.dto.ShortSubjectDTO;
import com.dbserver.voting.dto.ShortVotingDTO;
import com.dbserver.voting.dto.SubjectDTO;
import com.dbserver.voting.dto.VotingDTO;
import com.dbserver.voting.service.IAssemblyService;
import com.dbserver.voting.service.ISubjectAssemblyService;
import com.dbserver.voting.service.ISubjectService;
import com.dbserver.voting.service.IVotingService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@WebMvcTest(controllers = {
    VotingController.class,
    SubjectAssemblyController.class,
    AssemblyController.class,
    SubjectController.class})
@AutoConfigureRestDocs
public class VotingControllerTest extends ControllerTest {

    @SuppressWarnings("removal")
    @MockBean
    private IVotingService votingService;

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
    @DisplayName("Should return 201 for POST /voting")
    void shouldReturn201ForVotingCreation() throws Exception {
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

        var shortVotingDTO = new ShortVotingDTO(
                                UUID.randomUUID(),
                                shortSubjectDTO.id(),
                                Duration.ofMinutes(10),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null);
        var now = OffsetDateTime.now().minus(Duration.ofSeconds(1));
        var shortVotingDTO_ = new ShortVotingDTO(
                                shortVotingDTO.id(),
                                shortSubjectDTO.id(),
                                shortVotingDTO.votingInterval(),
                                now,
                                now.plus(shortVotingDTO.votingInterval()),
                                VotingStatus.OPEN,
                                null,
                                0,
                                0);
        when(votingService.registerVoting(any(ShortVotingDTO.class))).thenReturn(shortVotingDTO_);

        mockMvc.perform(
                    post("/voting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortVotingDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.subject").isNotEmpty())
                .andExpect(jsonPath("$.votingInterval").isNotEmpty())
                .andDo(document(
                    "voting-create",
                    requestFields(ShortVotingDTODescriptor.getRequestFieldsDescriptor()),
                    responseFields(ShortVotingDTODescriptor.getResponseFieldsDescriptor())));
    }

    @Test
    @DisplayName("Should return 200 for GET /voting/{id}")
    void shouldReturn200ForGetVotingById() throws Exception {
        var assemblyDTO = new AssemblyDTO(UUID.randomUUID(), LocalDate.now());
        var subjectDTO = new SubjectDTO(UUID.randomUUID(), "Headline" , "Description description description", null);
        var shortSubjectDTO = subjectDTO.toShortDTO();
        var shortSubjectAssemblyDTO = new ShortSubjectAssemblyDTO(subjectDTO.id(), assemblyDTO.id());

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

        var shortVotingDTO = new ShortVotingDTO(
                                UUID.randomUUID(),
                                shortSubjectDTO.id(),
                                Duration.ofMinutes(10),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null);
        var now = OffsetDateTime.now();
        var votingDTO = new VotingDTO(
                                shortVotingDTO.id(),
                                subjectDTO,
                                shortVotingDTO.votingInterval(),
                                now,
                                now.plus(shortVotingDTO.votingInterval()),
                                VotingStatus.OPEN,
                                null,
                                0,
                                0);
        when(votingService.registerVoting(any(ShortVotingDTO.class))).thenReturn(shortVotingDTO);
        mockMvc.perform(
                    post("/voting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortVotingDTO)))
                .andExpect(status().isCreated());
        when(votingService.getVotingById(eq(votingDTO.id()))).thenReturn(Optional.of(votingDTO));

        mockMvc.perform(
                    get("/voting/{id}", votingDTO.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(votingDTO.id().toString()))
                .andExpect(jsonPath("$.subject").isNotEmpty())
                .andExpect(jsonPath("$.votingInterval").isNotEmpty())
                .andDo(document(
                    "voting-get",
                    pathParameters(
                        parameterWithName("id").description("The ID of the Voting")),
                    responseFields(VotingDTODescriptor.getResponseFieldsDescriptor())));
    }

    @Test
    @DisplayName("Should return 200 for GET /voting")
    void shouldReturn200ForGetAllVoting() throws Exception {
        var assemblyDTO = new AssemblyDTO(UUID.randomUUID(), LocalDate.now());
        var subjectDTO = new SubjectDTO(UUID.randomUUID(), "Headline" , "Description description description", null);
        var shortSubjectDTO = subjectDTO.toShortDTO();
        var shortSubjectAssemblyDTO = new ShortSubjectAssemblyDTO(subjectDTO.id(), assemblyDTO.id());

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

        var votingList = new ArrayList<VotingDTO>();
        var votingCount = 3;
        var millis = 50;
        for (int i = 0; i < votingCount; i++) {
            var shortVotingDTO = new ShortVotingDTO(
                                UUID.randomUUID(),
                                shortSubjectDTO.id(),
                                Duration.ofMillis(millis),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null);
            var now = OffsetDateTime.now();
            votingList.add(new VotingDTO(
                                shortVotingDTO.id(),
                                subjectDTO,
                                shortVotingDTO.votingInterval(),
                                now,
                                now.plus(shortVotingDTO.votingInterval()),
                                VotingStatus.OPEN,
                                null,
                                0,
                                0));
            when(votingService.registerVoting(any(ShortVotingDTO.class))).thenReturn(shortVotingDTO);
            mockMvc.perform(
                    post("/voting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortVotingDTO)))
                .andExpect(status().isCreated());
            Thread.sleep(millis);
        }
        when(votingService.getAllVoting()).thenReturn(votingList);

        mockMvc.perform(
                    get("/voting")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(equalTo(votingList.size())))
                .andDo(document("voting-list"));
    }

    @Test
    @DisplayName("Should return 200 for PUT /voting/{id}")
    void shouldReturn200ForUpdateVoting() throws Exception {
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

        var shortVotingDTO1 = new ShortVotingDTO(
                                UUID.randomUUID(),
                                shortSubjectDTO.id(),
                                Duration.ofMinutes(10),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null);
        when(votingService.registerVoting(any(ShortVotingDTO.class))).thenReturn(shortVotingDTO1);
        mockMvc.perform(
                    post("/voting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortVotingDTO1)))
                .andExpect(status().isCreated());

        var shortVotingDTO2 = new ShortVotingDTO(
                                shortVotingDTO1.id(),
                                shortSubjectDTO.id(),
                                Duration.ofMinutes(20),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null);
        var now = OffsetDateTime.now();
        var shortVotingDTO2_ = new ShortVotingDTO(
                                shortVotingDTO2.id(),
                                shortSubjectDTO.id(),
                                shortVotingDTO2.votingInterval(),
                                now,
                                now.plus(shortVotingDTO2.votingInterval()),
                                VotingStatus.OPEN,
                                null,
                                0,
                                0);
        when(votingService.updateVoting(eq(shortVotingDTO1.id()), any(ShortVotingDTO.class))).thenReturn(shortVotingDTO2_);
                
        mockMvc.perform(
                    put("/voting/{id}", shortVotingDTO1.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortVotingDTO2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shortVotingDTO1.id().toString()))
                .andExpect(jsonPath("$.subject").isNotEmpty())
                .andExpect(jsonPath("$.votingInterval").value(shortVotingDTO2_.votingInterval().toString()))
                .andDo(document(
                    "voting-update",
                    pathParameters(
                        parameterWithName("id").description("The ID of the Voting")),
                    requestFields(ShortVotingDTODescriptor.getRequestFieldsDescriptor()),
                    responseFields(ShortVotingDTODescriptor.getResponseFieldsDescriptor())));
    }

    @Test
    @DisplayName("Should return 204 for PUT /voting/{id}/close")
    void shouldReturn204ForCloseVoting() throws Exception {
        var assemblyDTO = new AssemblyDTO(UUID.randomUUID(), LocalDate.now());
        var subjectDTO = new SubjectDTO(UUID.randomUUID(), "Headline" , "Description description description", null);
        var shortSubjectDTO = subjectDTO.toShortDTO();
        var shortSubjectAssemblyDTO = new ShortSubjectAssemblyDTO(subjectDTO.id(), assemblyDTO.id());

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

        var shortVotingDTO = new ShortVotingDTO(
                                UUID.randomUUID(),
                                shortSubjectDTO.id(),
                                Duration.ofMinutes(10),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null);
        when(votingService.registerVoting(any(ShortVotingDTO.class))).thenReturn(shortVotingDTO);
        mockMvc.perform(
                    post("/voting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortVotingDTO)))
                .andExpect(status().isCreated());

        var now = OffsetDateTime.now();
        var votingDTO = new VotingDTO(
                                shortVotingDTO.id(),
                                subjectDTO,
                                shortVotingDTO.votingInterval(),
                                now,
                                now.plus(shortVotingDTO.votingInterval()),
                                VotingStatus.CLOSED,
                                null,
                                0,
                                0);
        mockMvc.perform(
                    put("/voting/{id}/close", shortVotingDTO.id()))
                .andExpect(status().isNoContent())
                .andDo(document(
                    "voting-close",
                    pathParameters(
                        parameterWithName("id").description("The ID of the Voting"))));
        when(votingService.getVotingById(eq(shortVotingDTO.id()))).thenReturn(Optional.of(votingDTO));

        mockMvc.perform(
                    get("/voting/{id}", shortVotingDTO.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shortVotingDTO.id().toString()))
                .andExpect(jsonPath("$.subject").isNotEmpty())
                .andExpect(jsonPath("$.votingInterval").value(votingDTO.votingInterval().toString()))
                .andExpect(jsonPath("$.status").value(votingDTO.status().toString()));
    }

    @Test
    @DisplayName("Should return 204 for PUT /voting/{id}/cancel")
    void shouldReturn204ForCancelVoting() throws Exception {
        var assemblyDTO = new AssemblyDTO(UUID.randomUUID(), LocalDate.now());
        var subjectDTO = new SubjectDTO(UUID.randomUUID(), "Headline" , "Description description description", null);
        var shortSubjectDTO = subjectDTO.toShortDTO();
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

        var shortVotingDTO = new ShortVotingDTO(
                                UUID.randomUUID(),
                                shortSubjectDTO.id(),
                                Duration.ofMinutes(10),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null);
        when(votingService.registerVoting(any(ShortVotingDTO.class))).thenReturn(shortVotingDTO);
        mockMvc.perform(
                    post("/voting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortVotingDTO)))
                .andExpect(status().isCreated());

        var now = OffsetDateTime.now();
        var votingDTO = new VotingDTO(
                                shortVotingDTO.id(),
                                subjectDTO,
                                shortVotingDTO.votingInterval(),
                                now,
                                now.plus(shortVotingDTO.votingInterval()),
                                VotingStatus.CANCELLED,
                                null,
                                0,
                                0);
        mockMvc.perform(
                    put("/voting/{id}/cancel", shortVotingDTO.id()))
                .andExpect(status().isNoContent())
                .andDo(document(
                    "voting-cancel",
                    pathParameters(
                        parameterWithName("id").description("The ID of the Voting"))));
        when(votingService.getVotingById(eq(shortVotingDTO.id()))).thenReturn(Optional.of(votingDTO));

        mockMvc.perform(
                    get("/voting/{id}", shortVotingDTO.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shortVotingDTO.id().toString()))
                .andExpect(jsonPath("$.subject").isNotEmpty())
                .andExpect(jsonPath("$.votingInterval").value(votingDTO.votingInterval().toString()))
                .andExpect(jsonPath("$.status").value(votingDTO.status().toString()));
    }    
}
