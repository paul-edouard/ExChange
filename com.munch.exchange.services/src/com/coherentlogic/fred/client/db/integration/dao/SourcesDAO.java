package com.coherentlogic.fred.client.db.integration.dao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coherentlogic.fred.client.core.domain.Sources;

import static com.coherentlogic.fred.client.db.integration.dao.DAOConstants.SOURCES_DAO;

/**
 * Data access pattern implementation for {@link Sources} objects.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
@Component(value=SOURCES_DAO)
@Transactional
public class SourcesDAO extends SerializableDAO<Sources> {

    @Override
    public Sources find(long primaryKey) {
        return find(Sources.class, primaryKey);
    }
}
