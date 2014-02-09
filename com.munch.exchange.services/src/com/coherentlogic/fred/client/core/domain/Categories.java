package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.util.Constants.CATEGORIES;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.coherentlogic.coherent.data.model.core.domain.IdentityBean;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * A domain class which represents
 * <a href="http://research.stlouisfed.org/fred2/categories">categories</a> of
 * economic data.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@Entity
@Table(name=CATEGORIES)
@XStreamAlias(CATEGORIES)
public class Categories extends IdentityBean {

    private static final long serialVersionUID = -8593888059697867997L;

    static final String CATEGORY_LIST_PROPERTY = "categoryList";

    @XStreamImplicit
    private List<Category> categoryList = null;

    /**
     * Getter method for the list of {@link Category} objects.
     */
    @OneToMany(cascade=CascadeType.ALL)
    public List<Category> getCategoryList () {
        return categoryList;
    }

    /**
     * Setter method for the list of {@link Category} objects.
     */
    public void setCategoryList (List<Category> categoryList) {

        List<Category> oldValue = this.categoryList;

        this.categoryList = categoryList;

        firePropertyChange(CATEGORY_LIST_PROPERTY, oldValue, categoryList);
    }
}
