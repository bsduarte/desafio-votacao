package com.dbserver.voting.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.dbserver.voting.dto.AssociatedDTO;

public interface IAssociatedService {
    /**
     * Retrieves all associated.
     *
     * @return a list of all associated
     */
    List <AssociatedDTO> getAllAssociated();

    /**
     * Retrieves an associated by their ID.
     *
     * @param id the ID of the associated
     * @return the associated with the given ID
     */
    Optional<AssociatedDTO> getAssociatedById(UUID id);

    /**
     * Registers a new associated.
     *
     * @param associatedDTO the associated to register
     * @return the registered associated
     */
    AssociatedDTO registerAssociated(AssociatedDTO associatedDTO);

    /**
     * Updates an existing associated.
     *
     * @param id the ID of the associated to update
     * @param associatedDTO the new data for the associated
     * @return the updated associated
     */
    AssociatedDTO updateAssociated(UUID id, AssociatedDTO associatedDTO);

    /**
     * Deletes an associated by their ID.
     *
     * @param id the ID of the associated to delete
     */
    void deleteAssociated(UUID id);
}
