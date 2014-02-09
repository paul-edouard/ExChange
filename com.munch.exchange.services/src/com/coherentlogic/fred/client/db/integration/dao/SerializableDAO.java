package com.coherentlogic.fred.client.db.integration.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.coherentlogic.coherent.data.model.core.domain.SerializableBean;

/**
 * A base class for building data access objects that provides access to the
 * {@link EntityManager} along with some basic CRUD methods.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
@Transactional
public abstract class SerializableDAO<T extends SerializableBean> {

    private static final Logger log =
        LoggerFactory.getLogger(SerializableDAO.class);

    @PersistenceContext
    private EntityManager entityManager;

    public SerializableDAO() {
        super();
    }

    public SerializableDAO(EntityManager entityManager) {
        super();
        this.entityManager = entityManager;
    }

    /**
     * Getter method for the {@link EntityManager}.
     *
     * @return A reference to the {@link EntityManager} assigned to this
     *  instance of {@link SerializableDAO}.
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Method delegates to the {@link EntityManager#persist(Object)} method.
     *
     * @param target The target class to merge (update).
     */
    public void persist (T target) {
        log.debug("persist: method begins; target: " + target);

        entityManager.persist(target);
    }

    /**
     * Method delegates to the {@link EntityManager#merge(Object)} method.
     *
     * @param target The target class to merge (update).
     *
     * @return The merged instance of T.
     */
    public T merge (T target) {
        log.debug("merge: method begins; target: " + target);

        return entityManager.merge(target);
    }

    /**
     * Method delegates to the {@link EntityManager#find(Class, Object)} method.
     *
     * @param targetClass The type of class to be returned.
     * @param primaryKey The primary key value.
     *
     * @return An instance of the targetClass or null if none exists.
     */
    public T find (
        Class<T> targetClass,
        Long primaryKey
    ) {
        log.debug("find: method begins; primaryKey: " + primaryKey);

        T result = entityManager.find(targetClass, primaryKey);

        return result;
    }

    /**
     * Method delegates to the {@link EntityManager#remove(Object)} method.
     *
     * @param target The object being removed.
     */
    public void remove (T target) {
        log.debug("remove: method begins; target: " + target);

        entityManager.remove(target);
    }

    /**
     * Method signature for finding the domain object of type T with the
     * specified primaryKey.
     *
     * @param primaryKey The primary key.
     *
     * @return An instance of the domain object of type T.
     */
    public abstract T find (long primaryKey);
}
