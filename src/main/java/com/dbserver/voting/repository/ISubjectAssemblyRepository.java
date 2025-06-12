package com.dbserver.voting.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbserver.voting.domain.SubjectAssemblyId;
import com.dbserver.voting.model.Assembly;
import com.dbserver.voting.model.Subject;
import com.dbserver.voting.model.SubjectAssembly;


public interface ISubjectAssemblyRepository extends JpaRepository<SubjectAssembly, SubjectAssemblyId> {
    List<SubjectAssembly> findBySubject(Subject subject);
    List<SubjectAssembly> findByAssembly(Assembly assembly);
}
