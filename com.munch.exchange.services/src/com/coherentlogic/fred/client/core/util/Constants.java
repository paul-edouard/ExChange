package com.coherentlogic.fred.client.core.util;

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

    public static final String SERIESS = "seriess",
        CATEGORIES = "categories",
        RELEASES = "releases",
        RELEASE_DATES = "release_dates",
        RELEASE_DATE = "release_date",
        RELEASE_NAME = "release_name",
        RELEASE = "release",
        RELEASE_TABLE = "release_table",
        CATEGORY = "category",
        OBSERVATIONS = "observations",
        OBSERVATION = "observation",
        SOURCES = "sources",
        SOURCE = "source",
        ORDER_BY = "order_by",
        SORT_ORDER = "sort_order",
        OUTPUT_TYPE = "output_type",
        FILE_TYPE = "file_type",
        COUNT = "count",
        OFFSET = "offset",
        LIMIT = "limit",
        CHILDREN = "children",
        DATES = "dates",
        RELATED = "related",
        SEARCH = "search",
        UPDATES = "updates",
        GROUP_ID = "group_id",
        CREATED = "created",
        SERIES_COUNT = "series_count",
        TAG_NAMES = "tag_names",
        TAG_GROUP_ID = "tag_group_id",
        TAG_SEARCH_TEXT = "tag_search_text",
        SERIES_SEARCH_TEXT = "series_search_text";

    public static final String ASC = "asc", DESC = "desc";

    public static final String
        DAILY = "d",
        WEEKLY = "w",
        BI_WEEKLY = "bw",
        MONTHLY = "m",
        QUARTERLY = "q",
        SEMI_ANNUALLY = "sa",
        ANNUALLY = "a",
        WEEKLY_ENDING_FRIDAY = "wef",
        WEEKLY_ENDING_THURSDAY = "weth",
        WEEKLY_ENDING_WEDNESDAY = "wew",
        WEEKLY_ENDING_TUESDAY = "wetu",
        WEEKLY_ENDING_MONDAY = "wem",
        BI_WEEKLY_ENDING_WEDNESDAY = "bwew",
        BI_WEEKLY_ENDING_MONDAY = "bwem"; 

    public static final String FULL_TEXT = "full_text", SERIES_ID = "series_id",
        SOURCE_ID = "source_id", RELEASE_ID = "release_id";

    public static final String SERIES = "series",
        ID = "id",
        REALTIME_START = "realtime_start",
        REALTIME_END = "realtime_end",
        TITLE = "title",
        OBSERVATION_START = "observation_start",
        OBSERVATION_END = "observation_end",
        FREQUENCY = "frequency",
        FREQUENCY_SHORT = "frequency_short",
        UNITS = "units",
        UNITS_SHORT = "units_short",
        SEASONAL_ADJUSTMENT = "seasonal_adjustment",
        GEOGRAPHY = "geography",
        SEASONAL_ADJUSTMENT_SHORT = "seasonal_adjustment_short",
        LAST_UPDATED = "last_updated",
        POPULARITY = "popularity",
        NOTES = "notes",
        NAME = "name",
        PRESS_RELEASE = "press_release",
        LINK = "link",
        PARENT_ID = "parent_id",
        VINTAGE_DATES = "vintage_dates",
        VINTAGE_DATE = "vintage_date",
        VINTAGE_DATE_ALT = "vintageDate", // Alternative, used as a property.
        DATE = "date",
        VALUE = "value",
        FILTER_VARIABLE = "filter_variable",
        FILTER_VALUE = "filter_value";

    public static final String
        IDENTITY_OBJECT = "identity_object",
        IDENTITY_VALUE = "identity_value",
        REALTIME_BOUND = "realtime_bound",
        LIMIT_VALUE = "limit_value",
        ORDER_BY_VALUE = "order_by_value",
        OFFSET_VALUE = "offset_value",
        DEFAULT_OBJECT = "default_object",
        OBSERVATION_DATE = "observation_date",
        OBSERVATION_VALUE = "observation_value",
        RELEASE_DATE_VALUE = "release_date_value",
        COUNT_VALUE = "count_value",
        UNITS_SHORT_VALUE = "units_short_value",
        UNITS_VALUE = "units_value",
        FREQUENCY_SHORT_VALUE = "frequency_short_value",
        FREQUENCY_VALUE = "frequency_value";

    public static final String
        ALL = "all",
        MACRO = "macro",
        REGIONAL = "regional";

    public static final String
        TAGS = "tags",
        TAG = "tag";

    public static final int DEFAULT_LIMIT = 1000;
}