package com.revolut.database.repository;

import javax.persistence.NoResultException;
import java.util.List;

/**
 * represent repository for database operations
 * contains info about CRUD methods available in this system
 * save is responsible for create and update operations
 * @param <T> entity for which CRUD operation would be performed
 */
public interface CrudRepository<T> {

    /**
     * retrieves all entities of type T in database
     * @return List of all entities of type T in database
     */
    List<T> getAll();

    /**
     * retrieves entity with given id from database
     * or throws exception if entity not found
     * @param id entity id
     * @return managed entity with given id
     * @throws NoResultException if entity was not found
     */
    T getById(Long id) throws NoResultException;

    /**
     * this method is for locking entity in database
     * it try to retieve information about entity with given id
     * and locks it with pessimistic write lock
     * @param id entity id
     * @return entity if it was found or null
     */
    T getByIdForUpdate(Long id);

    /**
     * saves given transaction entity
     * method should automatically handle both persisting and merging
     * @param entity entity
     * @return managed  entity
     */
    T save(T entity);

    /**
     * tries to delete entity with given id
     * @param id entity id
     * @return number of entities being affected
     */
    long deleteById(Long id);

}
