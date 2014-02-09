package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.util.Constants.FREQUENCY;
import static com.coherentlogic.fred.client.core.util.Constants.LAST_UPDATED;
import static com.coherentlogic.fred.client.core.util.Constants.OBSERVATION_END;
import static com.coherentlogic.fred.client.core.util.Constants.OBSERVATION_START;
import static com.coherentlogic.fred.client.core.util.Constants.POPULARITY;
import static com.coherentlogic.fred.client.core.util.Constants.REALTIME_END;
import static com.coherentlogic.fred.client.core.util.Constants.REALTIME_START;
import static com.coherentlogic.fred.client.core.util.Constants.RELEASE_DATE;
import static com.coherentlogic.fred.client.core.util.Constants.RELEASE_ID;
import static com.coherentlogic.fred.client.core.util.Constants.SEARCH_RANK;
import static com.coherentlogic.fred.client.core.util.Constants.SEASONAL_ADJUSTMENT;
import static com.coherentlogic.fred.client.core.util.Constants.SERIES_ID;
import static com.coherentlogic.fred.client.core.util.Constants.SOURCE_ID;
import static com.coherentlogic.fred.client.core.util.Constants.TITLE;
import static com.coherentlogic.fred.client.core.util.Constants.UNITS;
import static com.coherentlogic.fred.client.core.util.Constants.VINTAGE_DATE;
import static com.coherentlogic.fred.client.core.util.Constants.SERIES_COUNT;

/**
 * An enumeration that describes the how a result can be ordered.
 *
 * @see <a href="http://api.stlouisfed.org/docs/fred/releases.html">releases</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/releases_dates.html">
 * releases_dates</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/sources.html">sources</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/source_releases.html">
 * source_releases</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/category_series.html">
 * category_series</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/release_series.html">
 * release_series</a>
 * @see <a href="http://api.stlouisfed.org/docs/fred/series_search.html">
 * series_search</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public enum OrderBy {

    searchRank(SEARCH_RANK),
    seriesId(SERIES_ID),
    /**
     * Note this is response only and should not be set in the request.
     */
    sourceId(SOURCE_ID),
    /**
     * Note this is response only and should not be set in the request.
     */
    releaseId(RELEASE_ID),
    /**
     * Note this is response only and should not be set in the request.
     */
    releaseDate(RELEASE_DATE),
    title(TITLE),
    units(UNITS),
    frequency(FREQUENCY),
    seasonalAdjustment(SEASONAL_ADJUSTMENT),
    realtimeStart(REALTIME_START),
    realtimeEnd(REALTIME_END),
    lastUpdated(LAST_UPDATED),
    observationStart(OBSERVATION_START),
    observationEnd(OBSERVATION_END),
    popularity(POPULARITY),
    vintageDate(VINTAGE_DATE),
    seriesCount(SERIES_COUNT);

    private final String value;

    OrderBy (String value) {
        this.value = value;
    }

    @Override
    public String toString () {
        return value;
    }
}
