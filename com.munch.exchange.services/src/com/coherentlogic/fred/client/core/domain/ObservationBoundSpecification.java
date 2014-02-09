package com.coherentlogic.fred.client.core.domain;

import java.util.Date;

/**
 * A specification for domain classes that are bound by an observation start
 * and end date.
 *
 * @author <a href='mailto:support@coherentlogic.com'>Support</a>
 */
public interface ObservationBoundSpecification {

    static final String OBSERVATION_START_PROPERTY = "observationStart",
        OBSERVATION_END_PROPERTY = "observationEnd";

    /**
     * Setter method for the observation start date property.
     */
    void setObservationStart(Date observationStart);

    /**
     * Setter method for the observation end date property.
     */
    void setObservationEnd(Date observationEnd);

    /**
     * Getter method for the observation start date property.
     */
    Date getObservationStart();

    /**
     * Getter method for the observation end date property.
     */
    Date getObservationEnd();
}
