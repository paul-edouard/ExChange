package com.coherentlogic.fred.client.core.domain;

/**
 * A key that indicates the aggregation method used for frequency aggregation.
 * This parameter has no affect if the frequency parameter is not set.
 *
 * @see <a href="http://api.stlouisfed.org/docs/fred/series_observations.html
 *      #aggregation_method>Aggregation Method</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public enum AggregationMethod {

    /**
     * Average (the default)
     */
    avg,

    /**
     * Sum
     */
    sum,

    /**
     * End of period
     */
    eop;
}
