package com.coherentlogic.fred.client.core.builders;

import static com.coherentlogic.fred.client.core.util.Constants.CATEGORIES;
import static com.coherentlogic.fred.client.core.util.Constants.CATEGORY;
import static com.coherentlogic.fred.client.core.util.Constants.CHILDREN;
import static com.coherentlogic.fred.client.core.util.Constants.DATES;
import static com.coherentlogic.fred.client.core.util.Constants.DATE_FORMAT;
import static com.coherentlogic.fred.client.core.util.Constants.DATE_PATTERN;
import static com.coherentlogic.fred.client.core.util.Constants.OBSERVATIONS;
import static com.coherentlogic.fred.client.core.util.Constants.RELATED;
import static com.coherentlogic.fred.client.core.util.Constants.RELEASE;
import static com.coherentlogic.fred.client.core.util.Constants.RELEASES;
import static com.coherentlogic.fred.client.core.util.Constants.SEARCH;
import static com.coherentlogic.fred.client.core.util.Constants.SERIES;
import static com.coherentlogic.fred.client.core.util.Constants.SERIES_SEARCH_TEXT;
import static com.coherentlogic.fred.client.core.util.Constants.SOURCE;
import static com.coherentlogic.fred.client.core.util.Constants.SOURCES;
import static com.coherentlogic.fred.client.core.util.Constants.TAGS;
import static com.coherentlogic.fred.client.core.util.Constants.TAG_GROUP_ID;
import static com.coherentlogic.fred.client.core.util.Constants.TAG_NAMES;
import static com.coherentlogic.fred.client.core.util.Constants.TAG_SEARCH_TEXT;
import static com.coherentlogic.fred.client.core.util.Constants.UPDATES;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.coherentlogic.coherent.data.model.core.builders.AbstractQueryBuilder;
import com.coherentlogic.coherent.data.model.core.cache.CacheServiceProviderSpecification;
import com.coherentlogic.fred.client.core.domain.AggregationMethod;
import com.coherentlogic.fred.client.core.domain.FileType;
import com.coherentlogic.fred.client.core.domain.FilterValue;
import com.coherentlogic.fred.client.core.domain.FilterVariable;
import com.coherentlogic.fred.client.core.domain.Frequency;
import com.coherentlogic.fred.client.core.domain.OrderBy;
import com.coherentlogic.fred.client.core.domain.OutputType;
import com.coherentlogic.fred.client.core.domain.SearchType;
import com.coherentlogic.fred.client.core.domain.SortOrder;
import com.coherentlogic.fred.client.core.domain.TagGroupId;
import com.coherentlogic.fred.client.core.domain.Unit;
import com.coherentlogic.fred.client.core.exceptions.DateOutOfBoundsException;
import com.coherentlogic.fred.client.core.exceptions.InvalidDateFormatException;
import com.coherentlogic.fred.client.core.exceptions.InvalidParameterValue;
import com.coherentlogic.fred.client.core.exceptions.LimitOutOfBoundsException;
import com.coherentlogic.fred.client.core.exceptions.OffsetOutOfBoundsException;

/**
 * Class that allows the developer to construct and execute a query to the
 * Federal Reserve Bank of St. Louis' FRED web services.
 * <p>
 * Note that this class is <b>not</b> thread-safe and cannot be used as a member
 * -level property -- if this is required, use the
 * {@link com.coherentlogic.fred.client.core.builders#RequestBuilderFactory
 * QueryBuilderFactory} class.
 * <p>
 * In order to facilitate method-chaining each setter method returns a reference
 * to this object.
 * <p>
 * For examples, refer to the {@link
 * com.coherentlogic.fred.client.core.builders#QueryBuilderTest
 * QueryBuilderTest} class.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class QueryBuilder extends AbstractQueryBuilder {

    private static final Logger log =
        LoggerFactory.getLogger(QueryBuilder.class);

    /**
     * @todo Move this message so that it appears in the AbstractQueryBuilder.
     */
    static {
        log.warn("***********************************************************");
        log.warn("*** Welcome to the Coherent Logic FRED Client version   ***");
        log.warn("***                 1.0.1-RELEASE.                      ***");
        log.warn("***                                                     ***");
        log.warn("***    Please take a moment to follow us on Twitter:    ***");
        log.warn("***                                                     ***");
        log.warn("***         https://twitter.com/CoherentMktData         ***");
        log.warn("***                                                     ***");
        log.warn("***    Feedback is appreciated so feel free to take     ***");
        log.warn("***    a short survey that will help us improve this    ***");
        log.warn("***                    framework.                       ***");
        log.warn("***                                                     ***");
        log.warn("***       http://www.surveymonkey.com/s/83HWTZF         ***");
        log.warn("***                                                     ***");
        log.warn("***********************************************************");
    }

    public static final String
        API_KEY = "api_key",
        CATEGORY_ID = "category_id",
        REALTIME_START = "realtime_start",
        REALTIME_END = "realtime_end",
        LIMIT = "limit",
        OFFSET = "offset",
        ORDER_BY = "order_by",
        SORT_ORDER = "sort_order",
        FILTER_VARIABLE = "filter_variable",
        FILTER_VALUE = "filter_value",
        INCLUDE_RELEASE_DATES_WITH_NO_DATA
            = "include_release_dates_with_no_data",
        RELEASE_ID = "release_id",
        OBSERVATION_START = "observation_start",
        OBSERVATION_END = "observation_end",
        UNITS = "units",
        FREQUENCY = "frequency",
        AGGREGATION_METHOD = "aggregation_method",
        OUTPUT_TYPE = "output_type",
        FILE_TYPE = "file_type",
        VINTAGE_DATES = "vintagedates",
        VINTAGE_DATES_PARAM = "vintage_dates",
        SERIES_ID = "series_id",
        SOURCE_ID = "source_id",
        SEARCH_TEXT = "search_text",
        SEARCH_TYPE = "search_type",
        SEMICOLON = ";";

    private static final Calendar MIN_DATE_CALENDAR, MAX_DATE_CALENDAR;

    static {
        MIN_DATE_CALENDAR = Calendar.getInstance();
        MIN_DATE_CALENDAR.set(Calendar.YEAR, 1776);
        MIN_DATE_CALENDAR.set(Calendar.MONTH, Calendar.JULY);
        MIN_DATE_CALENDAR.set(Calendar.DAY_OF_MONTH, 04);

        MAX_DATE_CALENDAR = Calendar.getInstance();
        MAX_DATE_CALENDAR.set(Calendar.YEAR, 9999);
        MAX_DATE_CALENDAR.set(Calendar.MONTH, Calendar.DECEMBER);
        MAX_DATE_CALENDAR.set(Calendar.DAY_OF_MONTH, 31);
    }

    /**
     * A constructor that takes a RestTemplate and a uri that points to the web
     * service endpoint.
     *
     * @param restTemplate Used to make the call to the web service and convert
     *  the XML into an instance of a domain class.
     *
     * @param uri The uri to the web service endpoint.
     *
     * @see {@link AbstractQueryBuilder}
     */
    public QueryBuilder (
        RestTemplate restTemplate,
        String uri
    ) {
        super (restTemplate, uri);
    }

    /**
     * A constructor that takes a RestTemplate, String (uri), and a Map which is
     * used to cache results returned from the call to the FRED web service.
     *
     * @param restTemplate Used to make the call to the web service and convert
     *  the XML into an instance of a domain class.
     *
     * @param uriBuilder Used to construct the URI.
     *
     * @param cache A cache implementation such as the JBoss <a
     *  href="http://www.jboss.org/infinispan/">Infinispan</a> cache.
     *
     * @see {@link AbstractQueryBuilder}
     */
    public QueryBuilder(
        RestTemplate restTemplate,
        String uri,
        CacheServiceProviderSpecification<String, Object> cache
    ) {
        super(restTemplate, uri, cache);
    }

    /**
     * A constructor that takes a RestTemplate and a UriBuilder.
     *
     * @param restTemplate Used to make the call to the web service and convert
     *  the XML into an instance of a domain class.
     *
     * @param uriBuilder Used to construct the URI.
     *
     * @see {@link AbstractQueryBuilder}
     */
    public QueryBuilder (
        RestTemplate restTemplate,
        UriBuilder uriBuilder
    ) {
        super (restTemplate, uriBuilder);
    }

    /**
     * A constructor that takes a RestTemplate, UriBuilder, and a Map which is
     * used to cache results returned from the call to the FRED web service.
     *
     * @param restTemplate Used to make the call to the web service and convert
     *  the XML into an instance of a domain class.
     *
     * @param uriBuilder Used to construct the URI.
     *
     * @param cache A cache implementation such as the JBoss <a
     *  href="http://www.jboss.org/infinispan/">Infinispan</a> cache.
     *
     * @see {@link AbstractQueryBuilder}
     */
    public QueryBuilder(
        RestTemplate restTemplate,
        UriBuilder uriBuilder,
        CacheServiceProviderSpecification<String, Object> cache
    ) {
        super(restTemplate, uriBuilder, cache);
    }

    /**
     * Extends the path to include series -- for example:
     *
     * http://api.stlouisfed.org/fred/series
     */
    public QueryBuilder series () {
        extendPathWith(SERIES);

        return this;
    }

    /**
     * Extends the path to include categories -- for example:
     *
     * http://api.stlouisfed.org/fred/categories
     */
    public QueryBuilder categories () {
        extendPathWith(CATEGORIES);

        return this;
    }

    /**
     * Extends the path to include category -- for example:
     *
     * http://api.stlouisfed.org/fred/category
     */
    public QueryBuilder category () {
        extendPathWith(CATEGORY);

        return this;
    }

    /**
     * Extends the path to include observations -- for example:
     *
     * http://api.stlouisfed.org/fred/observations
     */
    public QueryBuilder observations () {
        extendPathWith(OBSERVATIONS);

        return this;
    }

    /**
     * Extends the path to include releases -- for example:
     *
     * http://api.stlouisfed.org/fred/releases
     */
    public QueryBuilder releases () {
        extendPathWith(RELEASES);

        return this;
    }

    /**
     * Extends the path to include release -- for example:
     *
     * http://api.stlouisfed.org/fred/release
     */
    public QueryBuilder release () {
        extendPathWith(RELEASE);

        return this;
    }

    /**
     * Extends the path to include updates -- for example:
     *
     * http://api.stlouisfed.org/fred/updates
     */
    public QueryBuilder updates () {
        extendPathWith(UPDATES);

        return this;
    }

    /**
     * Extends the path to include vintagedates -- for example:
     *
     * http://api.stlouisfed.org/fred/vintagedates
     */
    public QueryBuilder vintageDates () {
        extendPathWith(VINTAGE_DATES);

        return this;
    }

    /**
     * Extends the path to include search -- for example:
     *
     * http://api.stlouisfed.org/fred/search
     */
    public QueryBuilder search () {
        extendPathWith(SEARCH);

        return this;
    }

    /**
     * Extends the path to include children -- for example:
     *
     * http://api.stlouisfed.org/fred/children
     */
    public QueryBuilder children () {
        extendPathWith(CHILDREN);

        return this;
    }

    /**
     * Extends the path to include related -- for example:
     *
     * http://api.stlouisfed.org/fred/related
     */
    public QueryBuilder related () {
        extendPathWith(RELATED);

        return this;
    }

    /**
     * Extends the path to include sources -- for example:
     *
     * http://api.stlouisfed.org/fred/sources
     */
    public QueryBuilder sources () {
        extendPathWith(SOURCES);

        return this;
    }

    /**
     * Extends the path to include source -- for example:
     *
     * http://api.stlouisfed.org/fred/source
     */
    public QueryBuilder source () {
        extendPathWith(SOURCE);

        return this;
    }

    /**
     * Extends the path to include dates -- for example:
     *
     * http://api.stlouisfed.org/fred/dates
     */
    public QueryBuilder dates () {
        extendPathWith(DATES);

        return this;
    }

    /**
     * Extends the path to include tags -- for example:
     *
     * http://api.stlouisfed.org/fred/tags
     */
    public QueryBuilder tags () {
        extendPathWith(TAGS);

        return this;
    }

    /**
     * Setter method for the API key parameter. Note the API key is requires by
     * every FRED web service.
     *
     * Register for an API key <a href="http://api.stlouisfed.org/api_key.html">
     * here</a>.
     *
     * @param apiKey For example, "abcdefghijklmnopqrstuvwxyz123456".
     */
    public QueryBuilder setApiKey (String apiKey) {

        addParameter(API_KEY, apiKey);

        return this;
    }

    /**
     * Setter method for the series id parameter.
     *
     * @param seriesId For example, "GNPCA".
     */
    public QueryBuilder setSeriesId (String seriesId) {

        put(SERIES_ID, seriesId);

        return this;
    }

    /**
     * Setter method for the release id parameter.
     *
     * @param releaseId For example, 53L.
     */
    public QueryBuilder setReleaseId (long releaseId) {

        put(RELEASE_ID, Long.toString(releaseId));

        return this;
    }

    /**
     * Setter method for the category id parameter.
     */
    public QueryBuilder setCategoryId (long categoryId) {

        put(CATEGORY_ID, Long.toString(categoryId));

        return this;
    }

    /**
     * Setter method for the source id parameter.
     */
    public QueryBuilder setSourceId (long sourceId) {

        put(SOURCE_ID, Long.toString(sourceId));

        return this;
    }

    /**
     * Setter method for the real-time start date parameter.
     */
    public QueryBuilder setRealtimeStart (Date realtimeStart) {

        boolean between = isBetween (realtimeStart);

        if (!between)
            throw new DateOutOfBoundsException(realtimeStart);

        DateFormat dateFormat = new SimpleDateFormat (DATE_FORMAT);

        String realtimeStartText = dateFormat.format(realtimeStart);

        put(REALTIME_START, realtimeStartText);

        return this;
    }

    /**
     * Setter method for the real-time start date parameter.
     *
     * @throws InvalidDateFormatException When the date does not conform to the
     *  expected format (ie. yyyy-MM-dd / 2012-06-21).
     */
    public QueryBuilder setRealtimeStart (String realtimeStart) {

        assertDateFormatIsValid ("realtimeStart", realtimeStart);

        put(REALTIME_START, realtimeStart);

        return this;
    }

    /**
     * Setter method for the real-time end date parameter.
     */
    public QueryBuilder setRealtimeEnd (Date realtimeEnd) {

        boolean between = isBetween (realtimeEnd);

        if (!between)
            throw new DateOutOfBoundsException(realtimeEnd);

        DateFormat dateFormat = new SimpleDateFormat (DATE_FORMAT);

        String realtimeEndText = dateFormat.format(realtimeEnd);

        put(REALTIME_END, realtimeEndText);

        return this;
    }

    /**
     * Setter method for the real-time end date parameter.
     *
     * @throws InvalidDateFormatException When the date does not conform to the
     *  expected format (ie. yyyy-MM-dd / 2012-06-21).
     */
    public QueryBuilder setRealtimeEnd (String realtimeEnd) {

        assertDateFormatIsValid ("realtimeEnd", realtimeEnd);

        put(REALTIME_END, realtimeEnd);

        return this;
    }

    /**
     * Setter method for the limit parameter.
     *
     * @param limit A value between 1 and 100000 (inclusive).
     */
    public QueryBuilder setLimit (long limit) {

        if (!(1 <= limit && limit <= 100000))
            throw new LimitOutOfBoundsException(limit);

        put(LIMIT, Long.toString(limit));

        return this;
    }

    /**
     * Setter method for the offset parameter.
     *
     * @param offset A non-negative integer.
     *
     * @throws OffsetOutOfBoundsException if the value of the offset is less
     *  than zero.
     */
    public QueryBuilder setOffset (int offset) {

        if (offset < 0)
            throw new OffsetOutOfBoundsException (offset);

        put(OFFSET, Integer.toString(offset));

        return this;
    }

    /**
     * Setter method for the order-by parameter.
     *
     * @see {@link OrderBy}
     */
    public QueryBuilder setOrderBy (OrderBy orderBy) {

        put(ORDER_BY, orderBy.toString());

        return this;
    }

    /**
     * Setter method for the sort order parameter.
     *
     * @see {@link SortOrder}
     */
    public QueryBuilder setSortOrder (SortOrder sortOrder) {

        put(SORT_ORDER, sortOrder.toString());

        return this;
    }

    /**
     * Setter method for the filter variable parameter.
     *
     * @see {@link FilterVariable}
     */
    public QueryBuilder setFilterVariable (FilterVariable filterVariable) {

        put(FILTER_VARIABLE, filterVariable.toString());

        return this;
    }

    /**
     * Setter method for the filter value parameter.
     *
     * @see {@link FilterValue}
     */
    public QueryBuilder setFilterValue (FilterValue filterValue) {

        put(FILTER_VALUE, filterValue.toString());

        return this;
    }

    /**
     * Setter method for the include-release-dates-with-no-data flag parameter.
     */
    public QueryBuilder setIncludeReleaseDatesWithNoData (boolean value) {

        put(INCLUDE_RELEASE_DATES_WITH_NO_DATA, Boolean.toString(value));

        return this;
    }

    /**
     * Setter method for the observation start date parameter.
     */
    public QueryBuilder setObservationStart (Date observationStart) {

        boolean between = isBetween (observationStart);

        if (!between)
            throw new DateOutOfBoundsException(observationStart);

        DateFormat dateFormat = new SimpleDateFormat (DATE_FORMAT);

        String observationStartText = dateFormat.format(observationStart);

        put(OBSERVATION_START, observationStartText);

        return this;
    }

    /**
     * Setter method for the observation start date parameter.
     *
     * @throws InvalidDateFormatException When the date does not conform to the
     *  expected format (ie. yyyy-MM-dd / 2012-06-21).
     */
    public QueryBuilder setObservationStart (String observationStart) {

        assertDateFormatIsValid ("observationStart", observationStart);

        put(OBSERVATION_START, observationStart);

        return this;
    }

    /**
     * Setter method for the observation end date parameter.
     *
     * @param observationEnd
     */
    public QueryBuilder setObservationEnd (Date observationEnd) {

        boolean between = isBetween (observationEnd);

        if (!between)
            throw new DateOutOfBoundsException(observationEnd);

        DateFormat dateFormat = new SimpleDateFormat (DATE_FORMAT);

        String observationEndText = dateFormat.format(observationEnd);

        put(OBSERVATION_END, observationEndText);

        return this;
    }

    /**
     * Setter method for the observation end date parameter parameter.
     *
     * @throws InvalidDateFormatException When the date does not conform to the
     *  expected format (ie. yyyy-MM-dd / 2012-06-21).
     */
    public QueryBuilder setObservationEnd (String observationEnd) {

        assertDateFormatIsValid ("observationEnd", observationEnd);

        put(OBSERVATION_END, observationEnd);

        return this;
    }

    /**
     * Setter method for the units parameter.
     *
     * @see {@link #Unit}
     */
    public QueryBuilder setUnits (Unit unit) {

        put(UNITS, unit.toString());

        return this;
    }

    /**
     * Setter method for the frequency parameter.
     *
     * @see {@link Frequency}
     */
    public QueryBuilder setFrequency (Frequency frequency) {

        put(FREQUENCY, frequency.toString());

        return this;
    }

    /**
     * Setter method for the aggregation method parameter.
     *
     * @see {@link AggregationMethod}
     */
    public QueryBuilder setAggregationMethod (
        AggregationMethod aggregationMethod) {

        put(AGGREGATION_METHOD, aggregationMethod.toString());

        return this;
    }

    /**
     * Setter method for the output type parameter.
     *
     * @see {@link OutputType}
     */
    public QueryBuilder setOutputType (OutputType outputType) {

        put(OUTPUT_TYPE, outputType.toString());

        return this;
    }

    /**
     * Setter method for the file type parameter.
     *
     * @see {@link FileType}
     */
    public QueryBuilder setFileType (FileType fileType) {

        put(FILE_TYPE, fileType.toString());

        return this;
    }

    /**
     * Setter method for the vinatage dates parameter, which can be a single
     * date, or a list of dates separated by a comma.
     *
     * Note that the format of the dates is not checked client-side -- if there
     * is an invalid date, the server will reject the call.
     */
    public QueryBuilder setVintageDates (String vintageDates) {

        put(VINTAGE_DATES_PARAM, vintageDates);

        return this;
    }

    /**
     * Setter method for the array of vintage dates parameter.
     *
     * @throws InvalidDateFormatException If any of the dates are not of the
     *  format yyyy-MM-dd.
     */
    public QueryBuilder setVintageDates (String... vintageDates) {

        String value = convertDates ("setVintageDates", ",", vintageDates);

        put(VINTAGE_DATES_PARAM, value);

        return this;
    }

    /**
     * Setter method for the search text  parameter which consists of the words
     * to match against economic data series.
     */
    public QueryBuilder setSearchText (String searchText) {

        put(SEARCH_TEXT, searchText);

        return this;
    }

    /**
     * Setter method for the series search text  parameter which consists of the
     * words to match against economic data series -- for example
     * "monetary service index".
     */
    public QueryBuilder setSeriesSearchText (String seriesSearchText) {

        put(SERIES_SEARCH_TEXT, seriesSearchText);

        return this;
    }

    /**
     * Setter method for the search type parameter.
     *
     * @see {@link SearchType}
     */
    public QueryBuilder setSearchType (SearchType searchType) {

        put(SEARCH_TYPE, searchType.toString());

        return this;
    }

    /**
     * Method takes a single string, such as "slovenia;food;oecd", and set this
     * as the tagName; this value filters results to match either tag
     * "slovenia", "food", or "oecd".
     *
     * @see http://api.stlouisfed.org/docs/fred/series_search_related_tags.html
     */
    public QueryBuilder setTagNames (String tagNames) {

        put(TAG_NAMES, tagNames);

        return this;
    }

    /**
     * An array of tags to filter results by -- optional, no filtering by tags
     * by default.
     *
     * For example: 'm1;m2' -- this value filters results to match either tag
     * 'm1' or tag 'm2'.
     *
     * This method takes an array of strings, such as "slovenia", "food", "oecd"
     * and creates a single aggregated string with each value separated by a
     * semicolon (ie. "slovenia;food;oecd").
     *
     * @see http://api.stlouisfed.org/docs/fred/series_search_related_tags.html
     */
    public QueryBuilder setTagNames (String... tagNames) {

        String result = combine (SEMICOLON, tagNames);

        return setTagNames (result);
    }

    /**
     * A tag group id to filter tags by type.
     *
     * String, optional, no filtering by tag group by default.
     * One of the following: 'freq', 'gen', 'geo', 'geot', 'rls', 'seas', 'src'.
     *
     * freq = Frequency
     * gen = General or Concept
     * geo = Geography
     * geot = Geography Type
     * rls = Release
     * seas = Seasonal Adjustment
     * src = Source
     */
    public QueryBuilder setTagGroupId (TagGroupId tagGroupId) {

        put (TAG_GROUP_ID, tagGroupId.toString());

        return this;
    }

    /**
     * The words to find matching tags with -- optional, no filtering by search
     * words by default.
     */
    public QueryBuilder setTagSearchText (String tagSearchText) {

        put (TAG_SEARCH_TEXT, tagSearchText);

        return this;
    }

    /**
     * Method adds a name-value pair to the internal list of name-value pairs.
     *
     * @param name The name of the parameter.
     * @param value The parameter value.
     */
    private void put (String name, String value) {
        addParameter(name, value);
    }

    static String combine (String seperator, String... values) {

        assertNotNullOrEmpty ("values", values);

        StringBuffer result = new StringBuffer ();

        int ctr = values.length;

        for (String next : values) {
            result.append(next);

            if (1 < ctr--) {
                result.append(seperator);
            }
        }
        return result.toString();
    }

    /**
     * @todo Move this method to the parent package.
     */
    static void assertNotNullOrEmpty (String variableName, Object[] values) {
        if (values == null || values.length == 0)
            throw new InvalidParameterValue ("The variable named '" +
                variableName + "' is either null or empty (" +
                ToStringBuilder.reflectionToString(values) + ").");
    }

    /**
     * Method checks that the actual date is between the
     * {@link #MIN_DATE_CALENDAR} and {@link #MAX_DATE_CALENDAR} dates.
     *
     * @param actual The actual date.
     *
     * @return True if the actual date is between the {@link #MIN_DATE_CALENDAR}
     *  and {@link #MAX_DATE_CALENDAR} dates.
     */
    private boolean isBetween (Date actual) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(actual);

        int minValue = calendar.compareTo(MIN_DATE_CALENDAR);
        int maxValue = calendar.compareTo(MAX_DATE_CALENDAR);

        boolean result = (0 <= minValue) && (maxValue <= 0);

        return result;
    }

    /**
     * Method converts the <i>dates</i> into a single String, with each date
     * separated by a comma.
     *
     * @param method The name of the method calling this method; this is used
     *  when an exception is thrown because one of the dates is in an invalid
     *  format.
     *
     * @param seperator The character which separates the dates.
     *
     * @param dates An array of dates. Note that each date must be in the format
     *  yyyy-MM-dd.
     *
     * @return A single string consisting of all dates separated by the
     *  separator.
     */
    static String convertDates (
        String method,
        String seperator,
        String... dates
    ) {
        StringBuffer buffer = new StringBuffer();

        if (dates != null && 0 < dates.length) {

            for (int ctr = 0; ctr < dates.length; ctr++) {

                assertDateFormatIsValid (method, dates[ctr]);

                buffer.append(dates[ctr]);

                if (ctr < dates.length - 1)
                    buffer.append(seperator);
            }
        }

        String result = (buffer.length() == 0) ? null : buffer.toString();

        return result;
    }

    /**
     * Method simply checks that the date format is valid and throws an
     * exception if it isn't.
     *
     * @param method The method that was invoked.
     *
     * @param date For example 2001-10-20, which is valid, or X001-1-2, which
     *  is invalid.
     *
     * @throws InvalidDateFormatException Whenever the date is invalid.
     */
    static void assertDateFormatIsValid (String method, String date) {
        Matcher matcher = DATE_PATTERN.matcher(date);

        if (!matcher.matches())
            throw new InvalidDateFormatException (
                "The date parameter " + date +" passed to the method " +
                method + " is invalid.");
    }
}
