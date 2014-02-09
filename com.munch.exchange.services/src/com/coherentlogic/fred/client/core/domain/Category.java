package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.domain.PropertyNames.NAME_PROPERTY;
import static com.coherentlogic.fred.client.core.util.Constants.CATEGORY;
import static com.coherentlogic.fred.client.core.util.Constants.NAME;
import static com.coherentlogic.fred.client.core.util.Constants.PARENT_ID;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.coherentlogic.coherent.data.model.core.domain.IdentityBean;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * A domain class which represents a category of economic data.
 *
 * Note that instances of this class will not have an id provided by the FRED
 * web services, however this class extends from {@link IdentityObject} because
 * we will need a primary key when saving instances of this class to the
 * database.
 *
 * @see <a href="http://api.stlouisfed.org/docs/fred/category.html">category</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/category_children.html">
 * category_children</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/category_related.html">
 * category_related</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/series_categories.html">
 * series_categories</a>
 * @see <a href="http://research.stlouisfed.org/fred2/categories/">
 * categories</a>
 * @see <a href="http://research.stlouisfed.org/">research.stlouisfed.org</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@Entity
@Table(name=CATEGORY)
@XStreamAlias(CATEGORY)
public class Category extends IdentityBean {

    private static final long serialVersionUID = 2622455224381507777L;

    static final String PARENT_ID_PROPERTY = "parentId";

    @XStreamAlias(NAME)
    @XStreamAsAttribute
    private String name = null;

    @XStreamAlias(PARENT_ID)
    @XStreamAsAttribute
    private String parentId = null;

    /**
     * Getter method for the name property.
     */
    public String getName () {
        return name;
    }

    /**
     * Setter method for the name property.
     */
    public void setName (String name) {

        String oldValue = this.name;

        this.name = name;

        firePropertyChange(NAME_PROPERTY, oldValue, name);
    }

    /**
     * Getter method for the parent id property.
     */
    public String getParentId () {
        return parentId;
    }

    /**
     * Setter method for the parent id property.
     */
    public void setParentId (String parentId) {

        String oldValue = this.parentId;

        this.parentId = parentId;

        firePropertyChange(PARENT_ID_PROPERTY, oldValue, parentId);
    }
}
