package com.coherentlogic.coherent.data.model.core.util;

import java.util.regex.Pattern;

/**
 * A class which contains various constants that are used in this project.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class Constants {

    public static final Pattern DATE_PATTERN
        = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String SEARCH_RANK = "search_rank";

    public static final String VALUE = "value";

    public static final String
        IDENTITY_BEAN = "identity_bean",
        IDENTITY_VALUE = "identity_value",
        ID = "id";

    public static final String
        ALL = "all",
        MACRO = "macro",
        REGIONAL = "regional";
}