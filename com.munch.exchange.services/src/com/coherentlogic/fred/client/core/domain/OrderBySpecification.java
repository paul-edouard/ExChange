package com.coherentlogic.fred.client.core.domain;

/**
 * A specification for domain classes that have an order-by property.
 *
 * @author <a href='mailto:support@coherentlogic.com'>Support</a>
 */
public interface OrderBySpecification {

    static final String
        ORDER_BY_PROPERTY = "orderBy";

    /**
     * Getter method for the order-by property.
     */
    OrderBy getOrderBy ();

    /**
     * Setter method for the order-by property.
     */
    void setOrderBy (OrderBy orderBy);
}
