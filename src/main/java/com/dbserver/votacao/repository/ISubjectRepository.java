package com.dbserver.votacao.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbserver.votacao.model.Subject;

public interface ISubjectRepository extends JpaRepository<Subject, UUID> {
}
