package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.util.Constants.FREQUENCY;
import static com.coherentlogic.fred.client.core.util.Constants.GEOGRAPHY;
import static com.coherentlogic.fred.client.core.util.Constants.SEASONAL_ADJUSTMENT;
import static com.coherentlogic.fred.client.core.util.Constants.UNITS;

/**
 * The attribute to filter results by.
 * <p>
 * Note that the filter variable is optional, no filter is assigned by default.
 * <p>
 * @see <a href="http://api.stlouisfed.org/docs/fred/category_series.html">
 * category_series</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/release_series.html">
 * release_series</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/series_search.html">
 * series_search</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public enum FilterVariable {

    frequency (FREQUENCY),
    units (UNITS),
    seasonalAdjustment (SEASONAL_ADJUSTMENT),
    /**
     * This is only used in the response -- for example, see the result which is
     * returned from the following call:
     * http://api.stlouisfed.org/fred/series/updates
     * ?api_key=
     * &realtime_start=2001-01-20
     * &realtime_end=2004-05-17
     * &limit=100
     * &offset=1
     * &filter_value=macro
     */
    geography (GEOGRAPHY);

    private final String value;

    FilterVariable (String value) {
        this.value = value;
    }

    @Override
    public String toString () {
        return value;
    }
}
