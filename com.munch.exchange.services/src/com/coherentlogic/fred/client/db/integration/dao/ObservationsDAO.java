package com.coherentlogic.fred.client.db.integration.dao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coherentlogic.fred.client.core.domain.Observations;

import static com.coherentlogic.fred.client.db.integration.dao.DAOConstants.OBSERVATIONS_DAO;

/**
 * Data access pattern implementation for {@link Observations} objects.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@Component(value=OBSERVATIONS_DAO)
@Transactional
public class ObservationsDAO extends SerializableDAO<Observations> {

    @Override
    public Observations find (long primaryKey) {
        return find(Observations.class, primaryKey);
    }
}
