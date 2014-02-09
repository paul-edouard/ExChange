package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.util.Constants.FULL_TEXT;
import static com.coherentlogic.fred.client.core.util.Constants.SERIES_ID;

/**
 * An enumeration that is used to determine the type of search to perform.
 *
 * @see <a href="http://api.stlouisfed.org/docs/fred/series_search.html">
 * series_search</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public enum SearchType {

    /**
     * Full text search type.
     */
    fullText (FULL_TEXT),

    /**
     * Series id search type.
     */
    seriesId (SERIES_ID);

    private final String value;

    SearchType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
