package com.dbserver.votacao.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbserver.votacao.model.Assembly;

public interface IAssemblyRepository extends JpaRepository<Assembly, UUID> {
}
