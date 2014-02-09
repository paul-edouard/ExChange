package com.coherentlogic.fred.client.core.domain;

import java.util.Date;

/**
 * A specification for domain classes that are bounded by a real-time start and
 * end date.
 *
 * @author <a href='mailto:support@coherentlogic.com'>Support</a>
 */
public interface RealtimeBoundSpecification {

    static final String REALTIME_START_PROPERTY = "realtimeStart",
        REALTIME_END_PROPERTY = "realtimeEnd";

    /**
     * Setter method for the real-time start date property.
     */
    void setRealtimeStart(Date realtimeStart);

    /**
     * Setter method for the real-time end date property.
     */
    void setRealtimeEnd(Date realtimeEnd);

    /**
     * Getter method for the real-time start date property.
     */
    Date getRealtimeStart();

    /**
     * Getter method for the real-time end date property.
     */
    Date getRealtimeEnd();
}
