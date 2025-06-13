package com.dbserver.voting.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dbserver.voting.model.SubjectAssemblyDTO;
import com.dbserver.voting.service.ISubjectAssemblyService;

@RestController
@RequestMapping("${path.subject-assembly}")
public class SubjectAssemblyController {

    private final ISubjectAssemblyService subjectAssemblyService;
    public SubjectAssemblyController(ISubjectAssemblyService subjectAssemblyService) {
        this.subjectAssemblyService = subjectAssemblyService;
    }

    @GetMapping
    public List<SubjectAssemblyDTO> getAllSubjectAssemblies() {
        return subjectAssemblyService.getAllSubjectAssemblies();
    }

    @PostMapping
    public SubjectAssemblyDTO registerSubjectAssembly(@RequestBody SubjectAssemblyDTO subjectAssemblyDTO) {
        return subjectAssemblyService.registerSubjectAssembly(subjectAssemblyDTO);
    }

    @DeleteMapping
    public void deleteSubjectAssembly(@RequestBody SubjectAssemblyDTO subjectAssemblyDTO) {
        subjectAssemblyService.deleteSubjectAssembly(subjectAssemblyDTO);
    }
}
