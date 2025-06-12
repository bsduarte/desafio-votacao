package com.dbserver.voting.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.dbserver.voting.model.Associated;

public interface IAssociatedRepository extends JpaRepository<Associated, UUID> {
    @Modifying
    @Transactional
    @Query("UPDATE Associated a SET a.active = false WHERE a.id = :id")
    void softDeleteById(@Param("id") UUID id);
}
