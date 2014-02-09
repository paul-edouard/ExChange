package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.util.Constants.ASC;
import static com.coherentlogic.fred.client.core.util.Constants.DESC;

/**
 * The SortOrder is used to sort results in either ascending or descending
 * observation_date order. Note that the sort order is optional and is set to
 * either 'asc' or 'desc' and defaults to 'asc'.
 *
 * @see <a href="http://api.stlouisfed.org/docs/fred/series_vintagedates.html">
 * series_vintagedates</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/series_observations.html">
 * series_observations</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/release_dates.html">
 * release_dates</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/releases_dates.html">
 * releases_dates</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/releases.html">releases</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/sources.html">sources</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/release_series.html">
 * release_series</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/series_search.html">
 * series_search</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public enum SortOrder {

    asc(ASC),
    desc(DESC);

    private final String value;

    private SortOrder(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
