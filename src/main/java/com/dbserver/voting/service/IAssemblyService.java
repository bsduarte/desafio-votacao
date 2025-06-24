package com.dbserver.voting.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.dbserver.voting.dto.AssemblyDTO;

public interface IAssemblyService {
    /**
     * Retrieves all assemblies.
     *
     * @return a list of all assembly
     */
    List<AssemblyDTO> getAllAssemblies();

    /**
     * Retrieves an assembly by their ID.
     *
     * @param id the ID of the assembly
     * @return the assembly with the given ID
     */
    Optional<AssemblyDTO> getAssemblyById(UUID id);

    /**
     * Registers a new assembly.
     *
     * @param assemblyDTO the assembly to register
     * @return the registered assembly
     */
    AssemblyDTO registerAssembly(AssemblyDTO assemblyDTO);

    /**
     * Updates an existing assembly.
     *
     * @param id the ID of the assembly to update
     * @param assemblyDTO the new data for the assembly
     * @return the updated assembly
     */
    Optional<AssemblyDTO> updateAssembly(UUID id, AssemblyDTO assemblyDTO);

    /**
     * Deletes an assembly by their ID.
     *
     * @param id the ID of the assembly to delete
     */
    void deleteAssembly(UUID id);
}
