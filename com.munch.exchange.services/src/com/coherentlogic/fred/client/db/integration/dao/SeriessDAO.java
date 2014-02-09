package com.coherentlogic.fred.client.db.integration.dao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coherentlogic.fred.client.core.domain.Seriess;

import static com.coherentlogic.fred.client.db.integration.dao.DAOConstants.SERIESS_DAO;

/**
 * Data access pattern implementation for {@link Seriess} objects.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
@Component(value=SERIESS_DAO)
@Transactional
public class SeriessDAO extends SerializableDAO<Seriess> {

    @Override
    public Seriess find (long primaryKey) {
        return find(Seriess.class, primaryKey);
    }
}
