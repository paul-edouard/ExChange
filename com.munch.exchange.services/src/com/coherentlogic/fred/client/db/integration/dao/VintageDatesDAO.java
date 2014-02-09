package com.coherentlogic.fred.client.db.integration.dao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coherentlogic.fred.client.core.domain.VintageDates;

import static com.coherentlogic.fred.client.db.integration.dao.DAOConstants.VINTAGE_DATES_DAO;

/**
 * Data access pattern implementation for {@link VintageDates} objects.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
@Component(value=VINTAGE_DATES_DAO)
@Transactional
public class VintageDatesDAO extends SerializableDAO<VintageDates> {

    @Override
    public VintageDates find(long primaryKey) {
        return find(VintageDates.class, primaryKey);
    }
}
