package com.dbserver.voting.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import com.dbserver.voting.descriptors.ShortVoteDTODescriptor;
import com.dbserver.voting.descriptors.VoteDTODescriptor;
import com.dbserver.voting.domain.VotingStatus;
import com.dbserver.voting.dto.AssemblyDTO;
import com.dbserver.voting.dto.AssociatedDTO;
import com.dbserver.voting.dto.ShortSubjectAssemblyDTO;
import com.dbserver.voting.dto.ShortVoteDTO;
import com.dbserver.voting.dto.ShortVotingDTO;
import com.dbserver.voting.dto.SubjectDTO;
import com.dbserver.voting.dto.VoteDTO;
import com.dbserver.voting.dto.VotingDTO;
import com.dbserver.voting.service.IAssemblyService;
import com.dbserver.voting.service.IAssociatedService;
import com.dbserver.voting.service.ISubjectAssemblyService;
import com.dbserver.voting.service.ISubjectService;
import com.dbserver.voting.service.IVoteService;
import com.dbserver.voting.service.IVotingService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.hamcrest.Matchers.equalTo;

@WebMvcTest(controllers = {
    VoteController.class,
    VotingController.class,
    SubjectAssemblyController.class,
    AssemblyController.class,
    SubjectController.class,
    AssociatedController.class})
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
public class VoteControllerTest extends ControllerTest {

    @SuppressWarnings("removal")
    @MockBean
    private IVoteService voteService;

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

    @SuppressWarnings("removal")
    @MockBean
    private IAssociatedService associatedService;

    @Test
    @DisplayName("Should return 201 for POST /vote")
    void shouldReturn201ForRegisterVote() throws Exception {
        var associatedDTO = new AssociatedDTO(UUID.randomUUID(), "AAA" , "aaa@gmail.com", "1111111111111", null);
        var assemblyDTO = new AssemblyDTO(UUID.randomUUID(), LocalDate.now());
        var subjectDTO = new SubjectDTO(UUID.randomUUID(), "Headline" , "Description", null);
        var shortSubjectAssemblyDTO = new ShortSubjectAssemblyDTO(subjectDTO.id(), assemblyDTO.id());
        var shortVotingDTO = new ShortVotingDTO(
                                UUID.randomUUID(),
                                subjectDTO.id(),
                                Duration.ofMinutes(10),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null); 
        mockMvc.perform(
                    post("/associated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(associatedDTO)))
                .andExpect(status().isCreated());
        mockMvc.perform(
                    post("/assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assemblyDTO)))
                    .andExpect(status().isCreated());
        mockMvc.perform(
                    post("/subject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subjectDTO)))
                    .andExpect(status().isCreated());
        mockMvc.perform(
                    post("/subject-assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortSubjectAssemblyDTO)))
                .andExpect(status().isCreated());
        mockMvc.perform(
                    post("/voting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortVotingDTO)))
                .andExpect(status().isCreated());

        var shortVoteDTO = new ShortVoteDTO(
                            UUID.randomUUID(),
                            shortVotingDTO.id(),
                            associatedDTO.id(),
                            true);
        when(voteService.registerVote(any(ShortVoteDTO.class))).thenReturn(shortVoteDTO);

        mockMvc.perform(
                    post("/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortVoteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(equalTo(shortVoteDTO.id().toString())))
                .andExpect(jsonPath("$.voting").value(equalTo(shortVoteDTO.voting().toString())))
                .andExpect(jsonPath("$.voteValue").value(equalTo(shortVoteDTO.voteValue())))
                .andDo(document(
                    "vote-create",
                    requestFields(ShortVoteDTODescriptor.getRequestFieldsDescriptor()),
                    responseFields(ShortVoteDTODescriptor.getResponseFieldsDescriptor())));
    }
    
    @Test
    @DisplayName("Should return 200 for GET /vote/{id}")
    void shouldReturn200ForGetVoteById() throws Exception {
        var associatedDTO = new AssociatedDTO(UUID.randomUUID(), "AAA" , "aaa@gmail.com", "1111111111111", null);
        var assemblyDTO = new AssemblyDTO(UUID.randomUUID(), LocalDate.now());
        var subjectDTO = new SubjectDTO(UUID.randomUUID(), "Headline" , "Description description description", null);
        var subjectSubjectAssemblyDTO = new ShortSubjectAssemblyDTO(subjectDTO.id(),assemblyDTO.id());
        var shortVotingDTO = new ShortVotingDTO(
                                UUID.randomUUID(),
                                subjectDTO.id(),
                                Duration.ofMinutes(10),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null);
        mockMvc.perform(
                    post("/associated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(associatedDTO)))
                .andExpect(status().isCreated());
        mockMvc.perform(
                    post("/assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assemblyDTO)))
                    .andExpect(status().isCreated());
        mockMvc.perform(
                    post("/subject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subjectDTO)))
                    .andExpect(status().isCreated());
        mockMvc.perform(
                    post("/subject-assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subjectSubjectAssemblyDTO)))
                .andExpect(status().isCreated());
        mockMvc.perform(
                    post("/voting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortVotingDTO)))
                .andExpect(status().isCreated());

        var aSecAgo = OffsetDateTime.now().minus(Duration.ofSeconds(1));
        var votingDTO = new VotingDTO(
                                shortVotingDTO.id(),
                                subjectDTO,
                                shortVotingDTO.votingInterval(),
                                aSecAgo,
                                aSecAgo.plus(shortVotingDTO.votingInterval()),
                                VotingStatus.OPEN,
                                null,
                                0,
                                0);
        var voteDTO = new VoteDTO(
                            UUID.randomUUID(),
                            votingDTO,
                            associatedDTO,
                            true);
        mockMvc.perform(
                    post("/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteDTO.toShortDTO())))
                .andExpect(status().isCreated());
        
        when(voteService.getVoteById(eq(voteDTO.id()))).thenReturn(Optional.of(voteDTO));

        mockMvc.perform(
                    get("/vote/{id}", voteDTO.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(equalTo(voteDTO.id().toString())))
                .andExpect(jsonPath("$.voting").isNotEmpty())
                .andExpect(jsonPath("$.voting.id").value(equalTo(voteDTO.voting().id().toString())))
                .andExpect(jsonPath("$.voteValue").value(equalTo(voteDTO.voteValue())))
                .andDo(document(
                    "vote-get",
                    pathParameters(
                        parameterWithName("id").description("The ID of the Vote")),
                    responseFields(VoteDTODescriptor.getResponseFieldsDescriptor())));
    }

    @Test
    @DisplayName("Should return 200 for GET /vote")
    void shouldReturn200ForGetAllVotes() throws Exception {
        var associatedDTO = new AssociatedDTO(UUID.randomUUID(), "AAA" , "aaa@gmail.com", "1111111111111", null);
        var assemblyDTO = new AssemblyDTO(UUID.randomUUID(), LocalDate.now());
        var subjectDTO = new SubjectDTO(UUID.randomUUID(), "Headline" , "Description description description", null);
        var shortSubjectAssemblyDTO = new ShortSubjectAssemblyDTO(subjectDTO.id(), assemblyDTO.id());
        var shortVotingDTO = new ShortVotingDTO(
                                UUID.randomUUID(),
                                subjectDTO.id(),
                                Duration.ofMinutes(10),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null);
        mockMvc.perform(
                    post("/associated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(associatedDTO)))
                .andExpect(status().isCreated());
        mockMvc.perform(
                    post("/assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assemblyDTO)))
                    .andExpect(status().isCreated());
        mockMvc.perform(
                    post("/subject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subjectDTO)))
                    .andExpect(status().isCreated());
        mockMvc.perform(
                    post("/subject-assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortSubjectAssemblyDTO)))
                .andExpect(status().isCreated());
        mockMvc.perform(
                    post("/voting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortVotingDTO)))
                .andExpect(status().isCreated());

        var aSecAgo = OffsetDateTime.now().minus(Duration.ofSeconds(1));
        var votingDTO = new VotingDTO(
                                shortVotingDTO.id(),
                                subjectDTO,
                                shortVotingDTO.votingInterval(),
                                aSecAgo,
                                aSecAgo.plus(shortVotingDTO.votingInterval()),
                                VotingStatus.OPEN,
                                null,
                                0,
                                0);
        var voteList = new ArrayList<VoteDTO>();
        var voteCount = 5;
        for (int i = 0; i < voteCount; i++) {
            var voteDTO = new VoteDTO(UUID.randomUUID(), votingDTO, associatedDTO, true);
            voteList.add(voteDTO);
            mockMvc.perform(
                    post("/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteDTO.toShortDTO())))
                .andExpect(status().isCreated());
        }
        when(voteService.getAllVotes()).thenReturn(voteList);

        mockMvc.perform(
                    get("/vote")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(equalTo(voteList.size())))
                .andDo(document("votes-list"));
    } 
}
