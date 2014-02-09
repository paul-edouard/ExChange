package com.coherentlogic.fred.client.db.integration.dao;

import static com.coherentlogic.fred.client.db.integration.dao.DAOConstants.TAGS_DAO;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coherentlogic.fred.client.core.domain.Tags;
import com.coherentlogic.fred.client.core.domain.VintageDates;

/**
 * Data access pattern implementation for {@link VintageDates} objects.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
@Component(value=TAGS_DAO)
@Transactional
public class TagsDAO extends SerializableDAO<Tags> {

    @Override
    public Tags find(long primaryKey) {
        return find(Tags.class, primaryKey);
    }
}
