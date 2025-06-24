package com.dbserver.voting.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbserver.voting.model.Assembly;

public interface IAssemblyRepository extends JpaRepository<Assembly, UUID> {
}
