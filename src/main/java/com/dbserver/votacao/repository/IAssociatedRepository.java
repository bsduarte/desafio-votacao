package com.dbserver.votacao.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbserver.votacao.model.Associated;

public interface IAssociatedRepository extends JpaRepository<Associated, UUID> {
}
