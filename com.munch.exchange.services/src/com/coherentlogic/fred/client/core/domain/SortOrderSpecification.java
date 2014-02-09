package com.coherentlogic.fred.client.core.domain;

/**
 * A specification for domain classes which have a sort order property.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public interface SortOrderSpecification {

    static final String SORT_ORDER_PROPERTY = "sortOrder";

    void setSortOrder (SortOrder sortOrder);

    SortOrder getSortOrder ();
}
