package com.coherentlogic.fred.client.core.domain;


/**
 * A specification for domain classes which have a filter property.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public interface FilterSpecification {

    static final String FILTER_VARIABLE_PROPERTY = "filterVariable",
        FILTER_VALUE_PROPERTY = "filterValue";

    /**
     * Getter method for the {@link FilterVariable} property.
     */
    FilterVariable getFilterVariable();

    /**
     * Setter method for the {@link FilterVariable} property.
     */
    void setFilterVariable(FilterVariable filterVariable);

    /**
     * Getter method for the {@link FilterValue} property.
     */
    FilterValue getFilterValue();

    /**
     * Setter method for the {@link FilterValue} property.
     */
    void setFilterValue(FilterValue filterValue);
}
