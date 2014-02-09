package com.coherentlogic.fred.client.db.integration.dao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coherentlogic.fred.client.core.domain.Categories;

import static com.coherentlogic.fred.client.db.integration.dao.DAOConstants.CATEGORIES_DAO;

/**
 * Data access pattern implementation for {@link Categories} objects.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@Component(value=CATEGORIES_DAO)
@Transactional
public class CategoriesDAO extends SerializableDAO<Categories> {

    @Override
    public Categories find (long primaryKey) {
        return find(Categories.class, primaryKey);
    }
}
