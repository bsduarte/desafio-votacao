package com.dbserver.voting.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbserver.voting.model.Associated;

public interface IAssociatedRepository extends JpaRepository<Associated, UUID> {
}
