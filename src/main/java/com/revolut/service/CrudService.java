package com.revolut.service;

import java.util.List;
import java.util.Optional;

/**
 * represent service with business logic for given entity
 * contains info about CRUD methods available in this system
 * save is responsible for create and update operations
 * @param <T> entity for which CRUD operation would be performed
 */
public interface CrudService<T> {

    /**
     * retrieves all entities of type T in system
     * @return List of all entities of type T in this system
     */
    List<T> findAll();

    /**
     *  retrieves information about specific entity from this system
     * @param id entity id
     * @return an {@link Optional} describing entity
     */
    Optional<T> getById(Long id);

    /**
     * saves given entity
     * @param entity data to save
     * @return an {@link Optional} describing entity
     */
    Optional<T> save(T entity);

    /**
     * tries to delete entity with given id
     * @param id entity id
     * @return boolean describing if entity was deleted
     */
    boolean deleteById(Long id);

}
