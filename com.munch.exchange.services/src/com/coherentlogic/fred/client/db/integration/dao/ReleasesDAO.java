package com.coherentlogic.fred.client.db.integration.dao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coherentlogic.fred.client.core.domain.Releases;

import static com.coherentlogic.fred.client.db.integration.dao.DAOConstants.RELEASES_DAO;

/**
 * Data access pattern implementation for {@link Releases} objects.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
@Component(value=RELEASES_DAO)
@Transactional
public class ReleasesDAO extends SerializableDAO<Releases> {

    @Override
    public Releases find(long primaryKey) {
        return find(Releases.class, primaryKey);
    }
}
