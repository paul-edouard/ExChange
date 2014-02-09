package com.coherentlogic.fred.client.core.domain;

import static com.coherentlogic.fred.client.core.util.Constants.FILE_TYPE;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * An enumeration that describes the file types available.
 *
 * @see {@link com.coherentlogic.fred.client.core.domain.Message}
 * @see <a href="http://api.stlouisfed.org/docs/fred/series_observations.html">
 * series_observations</a>
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
@XStreamAlias(FILE_TYPE)
public enum FileType {
    xml, txt, xls;
}
