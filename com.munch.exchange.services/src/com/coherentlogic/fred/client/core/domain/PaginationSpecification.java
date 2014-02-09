package com.coherentlogic.fred.client.core.domain;

/**
 * A specification for domain classes that can be paginated.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public interface PaginationSpecification {

    static final String OFFSET_PROPERTY = "offset", LIMIT_PROPERTY = "limit";

    /**
     * Setter method for the limit property.
     */
    void setLimit (long limit);

    /**
     * Setter method for the offset property.
     */
    void setOffset (int offset);

    /**
     * Getter method for the limit property.
     */
    long getLimit ();

    /**
     * Getter method for the offset property.
     */
    int getOffset ();
}
