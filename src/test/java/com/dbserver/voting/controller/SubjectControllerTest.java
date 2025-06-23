package com.dbserver.voting.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import com.dbserver.voting.descriptors.ShortSubjectDTODescriptor;
import com.dbserver.voting.descriptors.SubjectDTODescriptor;
import com.dbserver.voting.descriptors.SubjectResultsDTODescriptor;
import com.dbserver.voting.domain.VotingResult;
import com.dbserver.voting.domain.VotingStatus;
import com.dbserver.voting.dto.AssemblyDTO;
import com.dbserver.voting.dto.AssociatedDTO;
import com.dbserver.voting.dto.ShortSubjectAssemblyDTO;
import com.dbserver.voting.dto.ShortSubjectDTO;
import com.dbserver.voting.dto.ShortVoteDTO;
import com.dbserver.voting.dto.ShortVotingDTO;
import com.dbserver.voting.dto.SubjectDTO;
import com.dbserver.voting.dto.SubjectResultsDTO;
import com.dbserver.voting.service.IAssemblyService;
import com.dbserver.voting.service.IAssociatedService;
import com.dbserver.voting.service.ISubjectAssemblyService;
import com.dbserver.voting.service.ISubjectService;
import com.dbserver.voting.service.IVoteService;
import com.dbserver.voting.service.IVotingService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = {
    VoteController.class,
    VotingController.class,
    SubjectAssemblyController.class,
    AssemblyController.class,
    SubjectController.class,
    AssociatedController.class})
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
public class SubjectControllerTest extends ControllerTest {

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
    @DisplayName("Should return 201 for POST /subject")
    void shouldReturn201ForSubjectCreation() throws Exception {
        var shortSubjectDTO = new ShortSubjectDTO(UUID.randomUUID(), "Headline" , "Description");

        when(subjectService.registerSubject(any(ShortSubjectDTO.class))).thenReturn(shortSubjectDTO);

        mockMvc.perform(
                    post("/subject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortSubjectDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.headline").isNotEmpty())
                .andExpect(jsonPath("$.description").isNotEmpty())
                .andDo(document(
                    "subject-create",
                    requestFields(ShortSubjectDTODescriptor.getRequestFieldsDescriptor()),
                    responseFields(ShortSubjectDTODescriptor.getResponseFieldsDescriptor())));
    }

    @Test
    @DisplayName("Should return 200 for GET /subject/{id}")
    void shouldReturn200ForGetSubjectById() throws Exception {
        var subjectDTO = new SubjectDTO(UUID.randomUUID(), "Headline" , "Description", null);
        var shortSubjectDTO = subjectDTO.toShortDTO();
        when(subjectService.registerSubject(any(ShortSubjectDTO.class))).thenReturn(shortSubjectDTO);

        mockMvc.perform(
                    post("/subject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortSubjectDTO)))
                .andExpect(status().isCreated());
        when(subjectService.getSubjectById(eq(subjectDTO.id()))).thenReturn(Optional.of(subjectDTO));

        mockMvc.perform(
                    get("/subject/{id}", subjectDTO.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subjectDTO.id().toString()))
                .andExpect(jsonPath("$.headline").isNotEmpty())
                .andExpect(jsonPath("$.description").isNotEmpty())
                .andExpect(jsonPath("$.assemblies").value(nullValue()))
                .andDo(document(
                    "subject-get",
                    pathParameters(
                        parameterWithName("id").description("The ID of the Subject")),
                    responseFields(SubjectDTODescriptor.getResponseFieldsDescriptor())));
    }

    @Test
    @DisplayName("Should return 200 for GET /subject/{id}/results")
    void shouldReturn200ForGetSubjectResults() throws Exception {
        var assemblyDTO = new AssemblyDTO(UUID.randomUUID(), LocalDate.now());
        var shortSubjectDTO = new ShortSubjectDTO(UUID.randomUUID(), "Headline" , "Description");
        var shortSubjectAssemblyDTO = new ShortSubjectAssemblyDTO(shortSubjectDTO.id(), assemblyDTO.id());
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

        when(assemblyService.registerAssembly(any(AssemblyDTO.class))).thenReturn(assemblyDTO);
        when(subjectService.registerSubject(any(ShortSubjectDTO.class))).thenReturn(shortSubjectDTO);
        when(subjectAssemblyService.registerSubjectAssembly(any(ShortSubjectAssemblyDTO.class))).thenReturn(shortSubjectAssemblyDTO);
        when(votingService.registerVoting(any(ShortVotingDTO.class))).thenReturn(shortVotingDTO);

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
                    post("/voting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortVotingDTO)))
                .andExpect(status().isCreated());

        var votesInFavor = 0;
        var votesAgainst = 0;
        var voteCount = 51;
        for (int i = 0; i < voteCount; i++) {
            var associatedDTO = new AssociatedDTO(
                                        UUID.randomUUID(),
                                        "assoc" + i,
                                        i + "email@gmail.com",
                                        "111111111111" + i,
                                        null);
            var shortVoteDTO = new ShortVoteDTO(UUID.randomUUID(), shortVotingDTO.id(), associatedDTO.id(), (i % 2) == 0 ? true : false);

            when(associatedService.registerAssociated(any(AssociatedDTO.class))).thenReturn(associatedDTO);
            when(voteService.registerVote(any(ShortVoteDTO.class))).thenReturn(shortVoteDTO);

            mockMvc.perform(
                    post("/associated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(associatedDTO)))
                .andExpect(status().isCreated());            
            mockMvc.perform(
                    post("/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortVoteDTO)))
                .andExpect(status().isCreated());

            if (shortVoteDTO.voteValue()) votesInFavor++; else votesAgainst++;
        }

        var oneSecAgo = OffsetDateTime.now().minus(Duration.ofSeconds(1));
        var shortVotingDTO_ = new ShortVotingDTO(
                                    shortVotingDTO.id(),
                                    shortVotingDTO.subject(),
                                    shortVotingDTO.votingInterval(),
                                    oneSecAgo,
                                    oneSecAgo.plus(shortVotingDTO.votingInterval()),
                                    VotingStatus.OPEN,
                                    votesInFavor > votesAgainst ? VotingResult.ACCEPTED : 
                                        votesInFavor < votesAgainst ? VotingResult.REJECTED : VotingResult.UNDEFINED,
                                    votesInFavor,
                                    votesAgainst);
        var subjectResultDTO = new SubjectResultsDTO(
                                    shortSubjectDTO.id(),
                                    shortSubjectDTO.headline(),
                                    shortSubjectDTO.description(),
                                    List.of(shortVotingDTO_));

        when(subjectService.getSubjectResultsById(eq(shortSubjectDTO.id()))).thenReturn(Optional.of(subjectResultDTO));

        mockMvc.perform(
                    get("/subject/{id}/results", shortSubjectDTO.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(shortSubjectDTO.id().toString()))
                .andExpect(jsonPath("$.voting[0].id").value(shortVotingDTO.id().toString()))
                .andExpect(jsonPath("$.voting[0].votesInFavor").value(greaterThan(0)))
                .andExpect(jsonPath("$.voting[0].votesAgainst").value(greaterThan(0)))
                .andExpect(jsonPath("$.voting[0].result").isNotEmpty())
                .andDo(document(
                    "subject-results",
                    pathParameters(
                        parameterWithName("id").description("The Subject ID")),
                    responseFields(SubjectResultsDTODescriptor.getResponseFieldsDescriptor())));
    }


    @Test
    @DisplayName("Should return 200 for GET /subject")
    void shouldReturn200ForGetAllSubject() throws Exception {
        var subjectList = new ArrayList<SubjectDTO>();
        var subjectCount = 3;
        for (int i = 0; i < subjectCount; i++) {
            var subjectDTO = new SubjectDTO(UUID.randomUUID(), "Headline" + i , "Description" + i, null);
            var shortSubjectDTO = subjectDTO.toShortDTO();
            subjectList.add(subjectDTO);
            when(subjectService.registerSubject(any(ShortSubjectDTO.class))).thenReturn(shortSubjectDTO);
            mockMvc.perform(
                    post("/subject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortSubjectDTO)))
                .andExpect(status().isCreated());
        }
        when(subjectService.getAllSubjects()).thenReturn(subjectList);

        mockMvc.perform(
                    get("/subject")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(equalTo(subjectList.size())))
                .andDo(document("subjects-list"));
    }

    @Test
    @DisplayName("Should return 200 for PUT /subject/{id}")
    void shouldReturn200ForUpdateSubject() throws Exception {
        var shortSubjectDTO1 = new ShortSubjectDTO(UUID.randomUUID(), "Headline" , "Description");
        var shortSubjectDTO2 = new ShortSubjectDTO(shortSubjectDTO1.id(), "Headline2" , "Description2");

        when(subjectService.registerSubject(any(ShortSubjectDTO.class))).thenReturn(shortSubjectDTO1);

        mockMvc.perform(
                    post("/subject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortSubjectDTO1)))
                .andExpect(status().isCreated());
        when(subjectService.updateSubject(eq(shortSubjectDTO1.id()), any(ShortSubjectDTO.class))).thenReturn(shortSubjectDTO2);

        mockMvc.perform(
                    put("/subject/{id}", shortSubjectDTO1.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortSubjectDTO2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shortSubjectDTO1.id().toString()))
                .andExpect(jsonPath("$.headline").value(shortSubjectDTO2.headline().toString()))
                .andExpect(jsonPath("$.description").value(shortSubjectDTO2.description().toString()))
                .andDo(document(
                    "subject-update",
                    pathParameters(
                        parameterWithName("id").description("The Subject ID")),
                    requestFields(ShortSubjectDTODescriptor.getRequestFieldsDescriptor()),    
                    responseFields(ShortSubjectDTODescriptor.getResponseFieldsDescriptor())));
    }

    @Test
    @DisplayName("Should return 204 for DELETE /subject/{id}")
    void shouldReturn204ForDeleteSubject() throws Exception {
        var shortSubjectDTO = new ShortSubjectDTO(UUID.randomUUID(), "Headline" , "Description");
        when(subjectService.registerSubject(any(ShortSubjectDTO.class))).thenReturn(shortSubjectDTO);
        
        mockMvc.perform(
                    post("/subject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortSubjectDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(
                    delete("/subject/{id}", shortSubjectDTO.id()))
                .andExpect(status().isNoContent())
                .andDo(document(
                    "subject-delete",
                    pathParameters(
                        parameterWithName("id").description("The Subject ID"))));

        mockMvc.perform(
                    get("/subject/{id}", shortSubjectDTO.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
