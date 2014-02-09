package com.coherentlogic.fred.client.db.integration.dao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coherentlogic.fred.client.core.domain.ReleaseDates;

import static com.coherentlogic.fred.client.db.integration.dao.DAOConstants.RELEASE_DATES_DAO;

/**
 * Data access pattern implementation for {@link ReleaseDates} objects.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
@Component(value=RELEASE_DATES_DAO)
@Transactional
public class ReleaseDatesDAO extends SerializableDAO<ReleaseDates> {

    @Override
    public ReleaseDates find(long primaryKey) {
        return find(ReleaseDates.class, primaryKey);
    }
}
