package com.coherentlogic.fred.client.example;

import static com.coherentlogic.coherent.data.model.core.util.Utils.GMT_MINUS_6_TIMEZONE;
import static com.coherentlogic.coherent.data.model.core.util.Utils.TODAY;
import static com.coherentlogic.coherent.data.model.core.util.Utils.using;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.client.RestTemplate;

import com.coherentlogic.fred.client.core.builders.QueryBuilder;
import com.coherentlogic.fred.client.core.domain.AggregationMethod;
import com.coherentlogic.fred.client.core.domain.Categories;
import com.coherentlogic.fred.client.core.domain.Category;
import com.coherentlogic.fred.client.core.domain.FileType;
import com.coherentlogic.fred.client.core.domain.FilterValue;
import com.coherentlogic.fred.client.core.domain.FilterVariable;
import com.coherentlogic.fred.client.core.domain.Frequency;
import com.coherentlogic.fred.client.core.domain.Message;
import com.coherentlogic.fred.client.core.domain.Observation;
import com.coherentlogic.fred.client.core.domain.Observations;
import com.coherentlogic.fred.client.core.domain.OrderBy;
import com.coherentlogic.fred.client.core.domain.OutputType;
import com.coherentlogic.fred.client.core.domain.Release;
import com.coherentlogic.fred.client.core.domain.ReleaseDate;
import com.coherentlogic.fred.client.core.domain.ReleaseDates;
import com.coherentlogic.fred.client.core.domain.Releases;
import com.coherentlogic.fred.client.core.domain.SearchType;
import com.coherentlogic.fred.client.core.domain.Series;
import com.coherentlogic.fred.client.core.domain.Seriess;
import com.coherentlogic.fred.client.core.domain.SortOrder;
import com.coherentlogic.fred.client.core.domain.Source;
import com.coherentlogic.fred.client.core.domain.Sources;
import com.coherentlogic.fred.client.core.domain.Tag;
import com.coherentlogic.fred.client.core.domain.Tags;
import com.coherentlogic.fred.client.core.domain.Unit;
import com.coherentlogic.fred.client.core.domain.VintageDate;
import com.coherentlogic.fred.client.core.domain.VintageDates;

/**
 * Integration test for the QueryBuilder class.
 *
 * Note that if you want to run this test, you'll need to set your own
 * FRED_API_KEY as a key/value pair in you operating system's environment
 * variables, otherwise the call to <code>System.getenv(FRED_API_KEY)</code>,
 * below, will return null, and all tests will fail.
 *
 * WARNING: Some of these numbers change over time, which is why we've changed
 *          the unit tests such that they simply check for null, otherwise we
 *          have tests which will pass for a few days/weeks and then they fail
 *          because, for example 20 is now 19.
 *
 * WARNING: The dates may appear to be off by one day however this is due to the
 *          time zone -- ie. in winter the time is off by one hour.
 *
 * @see http://osdir.com/ml/java.xstream.user/2007-01/msg00058.html
 * @see http://www.timeanddate.com/library/abbreviations/timezones/na/cst.html
 * @see http://en.wikipedia.org/wiki/Daylight_saving_time
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class QueryBuilderTest {

    static final String FRED_API_KEY = "FRED_API_KEY",
        FRED_REST_TEMPLATE_ID = "fredRestTemplate";

    static final Date realtimeStart = using (2001, Calendar.JANUARY, 20);
    static final Date realtimeEnd = using (2004, Calendar.MAY, 17);

    /**
     * This value should be set in the environment properties of the operating
     * system. Make sure to restart your IDE and/or OS shell once this has been
     * set.
     */
    static final String API_KEY = "a28aa79a79d4fddce67d87b7743848a9"; // System.getenv(FRED_API_KEY);

    private final ApplicationContext context
        = new FileSystemXmlApplicationContext (
            "resources/spring/application-context.xml");

    private RestTemplate restTemplate = null;

    @Before
    public void setUp () throws Exception {
        restTemplate = (RestTemplate) context.getBean (FRED_REST_TEMPLATE_ID);
    }

    @After
    public void tearDown () throws Exception {
        restTemplate = null;
    }

    /**
     * For some reason using TODAY's date continues to return a result which is
     * off by one day, and I'm not sure why this is the case, however the
     * time zone setting may have something to do with this.
     */
    static void assertDateIsAccurateForToday (
        Date actual
    ) {
        Date expected = TODAY.getTime();

        assertDateIsAccurate (expected, actual);
    }

    static void assertDateIsAccurate (
        Date expected,
        Date actual
    ) {
        DateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yy"
        );

        dateFormat.setTimeZone(GMT_MINUS_6_TIMEZONE);

        String expectedText = dateFormat.format(expected);

        String actualText = dateFormat.format(actual);

        assertEquals (expectedText, actualText);
    }

    /**
     *
     */
    @Test
    public void getSeries () {

        QueryBuilder builder = new QueryBuilder (
            restTemplate,
            "http://api.stlouisfed.org/fred/series");

        Seriess result = builder
            .setApiKey(API_KEY)
            .setSeriesId("GNPCA")
            .setRealtimeStart(realtimeStart)
            .setRealtimeEnd(realtimeEnd)
            .doGet (Seriess.class);

        Date realtimeStartDate = result.getRealtimeStart();
        Date realtimeEndDate = result.getRealtimeEnd();

        assertNotNull ("realtimeStart", realtimeStartDate);
        assertDateIsAccurate (
            realtimeStart,
            realtimeStartDate);
        assertDateIsAccurate (
            realtimeEnd,
            realtimeEndDate);

        List<Series> seriesList = result.getSeriesList();

        Series seriesOne = seriesList.get(1);

        assertEquals ("GNPCA", seriesOne.getId());
        assertEquals ("Real Gross National Product", seriesOne.getTitle());

        assertDateIsAccurate (
            using (1929, Calendar.JANUARY, 01),
            seriesOne.getObservationStart()
        );
        assertDateIsAccurate (
            using (2003, Calendar.JANUARY, 01),
            seriesOne.getObservationEnd()
        );
        
        
        
        assertEquals ("Annual", seriesOne.getFrequency());
        assertEquals ("A", seriesOne.getFrequencyShort());
        assertEquals ("Billions of Chained 2000 Dollars", seriesOne.getUnits());
        assertEquals ("Bil. of Chn. 2000 $", seriesOne.getUnitsShort());
        assertEquals (
            "Not Seasonally Adjusted", seriesOne.getSeasonalAdjustment());
        assertEquals ("NSA", seriesOne.getSeasonalAdjustmentShort());
        // We wont bother checking the time here.
        assertDateIsAccurate (
            using (2007, Calendar.JANUARY, 26),
            seriesOne.getLastUpdated());
        // Popularity may change so we'll just check for null.
        assertNotNull (seriesOne.getPopularity());
    }

    @Test
    public void getSeriesCategories () {

        QueryBuilder builder = new QueryBuilder (
            restTemplate,
            "http://api.stlouisfed.org/fred/series/categories"
        );

        Categories categories = builder
            .setApiKey(API_KEY)
            .setSeriesId("EXJPUS")
            .setRealtimeStart(realtimeStart)
            .setRealtimeEnd(realtimeEnd)
            .doGet (Categories.class);

        List<Category> categoryList = categories.getCategoryList();

        assertEquals (2, categoryList.size());

        Category cat275 = categoryList.get(1);

        assertEquals ("275", cat275.getId());
        assertEquals ("Japan", cat275.getName());
        assertEquals ("158", cat275.getParentId());
    }

    /**
     * Note that for some reason the getSeriesObservationsExpecting[?] tests
     * will fail if they're all running. Suspicion is on the server as the
     * source of the problem since these methods do work if they're run in
     * isolation.
     *
     * This test can be uncommented, however make sure you comment others as
     * these tests may fail, when in fact they should pass.
     */
//    @Test
    public void getSeriesObservationsExpectingXML () {

        QueryBuilder builder = new QueryBuilder (
            restTemplate,
            "http://api.stlouisfed.org/fred/series/observations"
        );
        
        //TODO
        
        Observations observations =
            builder
                .setApiKey(API_KEY)
                //.setSeriesId("GNPCA")
                .setSeriesId("NAPM")
                .doGet(Observations.class);

        Message content = observations.getMessage();

        assertNull (content);

        assertDateIsAccurateForToday(observations.getRealtimeStart());
        assertDateIsAccurateForToday(observations.getRealtimeEnd());
        assertDateIsAccurate(
            using (1776, Calendar.JULY, 4),
            observations.getObservationStart()
        );
        assertDateIsAccurate(
            using (9999, Calendar.DECEMBER, 31),
            observations.getObservationEnd()
        );
        assertEquals(Unit.lin, observations.getUnits());
        assertEquals(
            OutputType.observationsByRealTimePeriod,
            observations.getOutputType());
        assertEquals(
            FileType.xml,
            observations.getFileType());
        assertEquals(SortOrder.asc, observations.getSortOrder());
       // assertEquals(84, observations.getCount());

        List<Observation> observationList = observations.getObservationList();
        for(Observation o: observationList){
        	System.out.println("Value: "+o.getValue()+", Date: "+o.getDate());
        }

        Observation obs2 = observationList.get(2);

        assertDateIsAccurateForToday(obs2.getRealtimeStart());
        assertDateIsAccurateForToday(obs2.getRealtimeEnd());
        assertDateIsAccurate(
            using (1931, Calendar.JANUARY, 01),
            obs2.getDate()
        );
       // assertEquals (new BigDecimal("912.1"), obs2.getValue());
    }

    /**
     * This test was passing and then on the 25th of June it started to fail and
     * I'm not sure why as nothing has changed client-side. Printing the
     * escaped URL and then pasting it into a browser results in a valid file
     * being returned.
     */
//    @Test
    public void getSeriesObservationsExpectingXLSFile () throws IOException {

        Date observationStart = realtimeStart;
        Date observationEnd = realtimeEnd;

        QueryBuilder builder = new QueryBuilder (
            restTemplate,
            "http://api.stlouisfed.org/fred/series/observations"
        );

        Observations observations =
            builder
                .setApiKey(API_KEY)
                .setSeriesId("GNPCA")
                .setRealtimeStart(realtimeStart)
                .setRealtimeEnd(realtimeEnd)
                .setLimit(10)
                .setOffset(5)
                .setSortOrder(SortOrder.desc)
                .setObservationStart(observationStart)
                .setObservationEnd(observationEnd)
                .setUnits(Unit.lin)
                .setFrequency(Frequency.annually)
                .setAggregationMethod(AggregationMethod.sum)
                .setOutputType(OutputType.observationsByRealTimePeriod)
                .setFileType(FileType.xls)
                .doGet(Observations.class);

        Message content = observations.getMessage();

        assertNotNull (content);

        InputStream in = content.getBody();

        assertTrue (0 < in.available());
    }

    /**
     * Note that for some reason the getSeriesObservationsExpecting[?] tests
     * will fail if they're all running. Suspicion is on the server as the
     * source of the problem since these methods do work if they're run in
     * isolation.
     *
     * This test can be uncommented, however make sure you comment others as
     * these tests may fail, when in fact they should pass.
     */
//    @Test
    public void getSeriesObservationsExpectingXmlFile () {

        Date observationStart = realtimeStart;
        Date observationEnd = realtimeEnd;

        QueryBuilder builder = new QueryBuilder (
            restTemplate,
            "http://api.stlouisfed.org/fred/series/observations"
        );

        Observations observations =
            builder
                .setApiKey(API_KEY)
                .setSeriesId("FINSLCA")
                .setRealtimeStart(realtimeStart)
                .setRealtimeEnd(realtimeEnd)
                .setLimit(10)
                .setOffset(0)
                .setSortOrder(SortOrder.desc)
                .setObservationStart(observationStart)
                .setObservationEnd(observationEnd)
                .setUnits(Unit.lin)
                .setFrequency(Frequency.annually)
                .setAggregationMethod(AggregationMethod.sum)
                .setOutputType(OutputType.observationsByRealTimePeriod)
                .setFileType(FileType.xml)
                .doGet(Observations.class);

        assertNotNull (observations);
        assertDateIsAccurate(
            realtimeStart,
            observations.getRealtimeStart()
        );
        assertDateIsAccurate(
            realtimeEnd,
            observations.getRealtimeEnd()
        );
        assertDateIsAccurate(
            realtimeStart,
            observations.getObservationStart()
        );
        assertDateIsAccurate(
            realtimeEnd,
            observations.getObservationEnd()
        );
        assertEquals(Unit.lin, observations.getUnits());
        assertEquals(SortOrder.desc, observations.getSortOrder());
        assertEquals(6, observations.getCount());
        assertEquals(0, observations.getOffset());
        assertEquals(10, observations.getLimit());

        List<Observation> observationList = observations.getObservationList();
        
        for(Observation o: observationList){
        	System.out.println("Value: "+o.getValue()+", Date: "+o.getDate());
        }
        
        Observation observation2 = observationList.get(2);

        assertDateIsAccurate(
            using (2003, Calendar.DECEMBER, 23),
            observation2.getRealtimeStart()
        );
        assertDateIsAccurate(
            using (2004, Calendar.MAY, 17),
            observation2.getRealtimeEnd()
        );
        assertDateIsAccurate(
            using (2002, Calendar.JANUARY, 01),
            observation2.getDate()
        );
        assertEquals(new BigDecimal ("10105.0"), observation2.getValue());

        Message content = observations.getMessage();

        assertNull(content.getBody());
    }

    /**
     * Note that for some reason the getSeriesObservationsExpecting[?] tests
     * will fail if they're all running. Suspicion is on the server as the
     * source of the problem since these methods do work if they're run in
     * isolation.
     *
     * This test can be uncommented, however make sure you comment others as
     * these tests may fail, when in fact they should pass.
     */
//    @Test
    public void getSeriesObservationsExpectingTxtFile () throws IOException {

        Date observationStart = realtimeStart;
        Date observationEnd = realtimeEnd;

        QueryBuilder builder = new QueryBuilder (
            restTemplate,
            "http://api.stlouisfed.org/fred/series/observations"
        );

        Observations observations =
            builder
                .setApiKey(API_KEY)
                .setSeriesId("GDPCA")
                .setRealtimeStart(realtimeStart)
                .setRealtimeEnd(realtimeEnd)
                .setLimit(10)
                .setOffset(0)
                .setSortOrder(SortOrder.desc)
                .setObservationStart(observationStart)
                .setObservationEnd(observationEnd)
                .setUnits(Unit.lin)
                .setFrequency(Frequency.annually)
                .setAggregationMethod(AggregationMethod.sum)
                .setOutputType(OutputType.observationsByRealTimePeriod)
                .setFileType(FileType.txt)
                .doGet(Observations.class);

        assertNotNull (observations);

        // Note that since we're returning a file, the observations properties
        // will not be set, as the data is in the file.
        Message content = observations.getMessage();

        assertNotNull (content);

        InputStream in = content.getBody();

        assertTrue (0 < in.available());
    }

    /**
     * http://api.stlouisfed.org/fred/series/release?series_id=IRA
     * &realtime_start=2001-01-20
     * &realtime_end=2004-05-17
     * &api_key=
     */
    @Test
    public void getSeriesRelease () {

        QueryBuilder builder = new QueryBuilder (
            restTemplate,
            "http://api.stlouisfed.org/fred/series/release"
        );

        Releases releases =
            builder
                .setApiKey(API_KEY)
                .setSeriesId("IRA")
                .setRealtimeStart(realtimeStart)
                .setRealtimeEnd(realtimeEnd)
                .doGet(Releases.class);

        assertDateIsAccurate(
            realtimeStart,
            releases.getRealtimeStart()
        );
        assertDateIsAccurate(
            realtimeEnd,
            releases.getRealtimeEnd()
        );

        List<Release> releaseList = releases.getReleaseList();

        Release release = releaseList.get(0);

        assertEquals(Long.valueOf(21L), release.getId());
        assertEquals("H.6 Money Stock and Debt Measures", release.getName());
        assertEquals(Boolean.TRUE, release.isPressRelease());
        assertEquals(
            "http://www.federalreserve.gov/releases/h6/", release.getLink());

        assertDateIsAccurate(
            realtimeStart,
            release.getRealtimeStart());
        assertDateIsAccurate(
            using (2002, Calendar.MAY, 01),
            release.getRealtimeEnd()
        );
    }

    /* #3
popularity="53" notes="Averages of daily data.  Copyright, 2011, Moody's Investor Services. Reprinted with permission.  Moody's tries to include bonds with remaining maturities as close as possible to 30 years. Moody's drops bonds if the remaining life falls below 20 years, if the bond is susceptible to redemption, or if the rating changes."/>
     */
    @Test
    public void getSeriesUpdates () {

        QueryBuilder builder = new QueryBuilder (
            restTemplate,
            "http://api.stlouisfed.org/fred/series/updates"
        );

        Seriess seriess =
            builder
                .setApiKey(API_KEY)
                .setRealtimeStart(realtimeStart)
                .setRealtimeEnd(realtimeEnd)
                .setLimit(100)
                .setOffset(1)
                .setFilterValue(FilterValue.macro)
                .doGet(Seriess.class);

        assertNotNull (seriess);

        List<Series> seriesList = seriess.getSeriesList();

        assertNotNull(seriesList);
        assertEquals(100, seriesList.size());

        Series series3 = seriesList.get(3);

        assertEquals("WBAA", series3.getId());
        assertDateIsAccurate(realtimeStart, series3.getRealtimeStart());
        assertDateIsAccurate(realtimeEnd, series3.getRealtimeEnd());
        assertEquals("Moody's Seasoned Baa Corporate Bond Yield",
            series3.getTitle());
        assertDateIsAccurate (using (1962, Calendar.JANUARY, 05),
            series3.getObservationStart());
        // Previously 02/Sept/20 -- review why this date changed.
        assertDateIsAccurate (using (2002, Calendar.OCTOBER, 18),
            series3.getObservationEnd());
        assertEquals("Weekly, Ending Friday", series3.getFrequency());
        assertEquals("W", series3.getFrequencyShort());
        assertEquals("Percent", series3.getUnits());
        assertEquals("%", series3.getUnitsShort());
        assertEquals("Not Seasonally Adjusted",
            series3.getSeasonalAdjustment());
        assertEquals("NSA",
            series3.getSeasonalAdjustmentShort());
        // Actual time is ignored at the moment.
        assertDateIsAccurate(using (2012, Calendar.NOVEMBER, 05),
            series3.getLastUpdated());
        // This may change.
        assertNotNull(series3.getPopularity());
        assertEquals("Averages of daily data.  Copyright, 2011, Moody's " +
            "Investor Services. Reprinted with permission.  Moody's tries " +
            "to include bonds with remaining maturities as close as " +
            "possible to 30 years. Moody's drops bonds if the remaining life " +
            "falls below 20 years, if the bond is susceptible to redemption, " +
            "or if the rating changes.",
            series3.getNotes());
    }

    @Test
    public void getVintageDates () {

        QueryBuilder builder = new QueryBuilder (
            restTemplate,
            "http://api.stlouisfed.org/fred/series/vintagedates"
        );

        VintageDates vintageDates =
            builder
                .setApiKey(API_KEY)
                .setSeriesId("GNPCA")
                .setRealtimeStart(realtimeStart)
                .setRealtimeEnd(realtimeEnd)
                .setLimit(100)
                .setOffset(1)
                .setSortOrder(SortOrder.desc)
                .doGet(VintageDates.class);

        assertNotNull (vintageDates);
        assertDateIsAccurate(
            using (2001, Calendar.JANUARY, 20),
            vintageDates.getRealtimeStart());
        assertDateIsAccurate(
            using (2004, Calendar.MAY, 17),
            vintageDates.getRealtimeEnd());
        assertEquals (OrderBy.vintageDate, vintageDates.getOrderBy());
        assertEquals (SortOrder.desc, vintageDates.getSortOrder());
        assertEquals (8, vintageDates.getCount());
        assertEquals (1, vintageDates.getOffset());
        assertEquals (100, vintageDates.getLimit());

        List<VintageDate> vintageDateList = vintageDates.getVintageDateList();

        assertEquals (7, vintageDateList.size ());

        for (VintageDate vintageDate : vintageDateList) {
            assertNotNull (vintageDate.getVintageDate());
        }
    }

    @Test
    public void getSeriesSearch () {

        QueryBuilder builder = new QueryBuilder(
            restTemplate,
            "http://api.stlouisfed.org/fred"
        );

        Seriess seriess = builder
            .setApiKey(API_KEY)
            .series()
            .search()
            .setSearchText("money stock")
            .setSearchType(SearchType.fullText)
            .setRealtimeStart(realtimeStart)
            .setRealtimeEnd(realtimeEnd)
            .setLimit(1000)
            .setOffset(1)
            .setOrderBy(OrderBy.searchRank)
            .setSortOrder(SortOrder.desc)
            .setFilterVariable(FilterVariable.frequency)
            .setFilterValue(FilterValue.all)
            .doGet(Seriess.class);

        assertNotNull (seriess);
        assertDateIsAccurate(
            using (2001, Calendar.JANUARY, 20),
            seriess.getRealtimeStart());
        assertDateIsAccurate(
            using (2004, Calendar.MAY, 17),
            seriess.getRealtimeEnd());
        assertEquals(FilterValue.all, seriess.getFilterValue());
        assertEquals(OrderBy.searchRank, seriess.getOrderBy());
        assertEquals(SortOrder.desc, seriess.getSortOrder());
        assertEquals(0, seriess.getCount());
        assertEquals(FilterValue.all, seriess.getFilterValue());
        assertEquals(1, seriess.getOffset());
        assertEquals(1000, seriess.getLimit());
    }

    /**
     * fred/category
     */
    @Test
    public void getCategory () {

        int categoryId = 125;

        QueryBuilder builder = new QueryBuilder(
            restTemplate,
            "http://api.stlouisfed.org/fred/category"
        );

        Categories categories = builder
            .setApiKey(API_KEY)
            .setCategoryId(categoryId)
            .doGet(Categories.class);

        assertNotNull (categories);

        List<Category> categoryList = categories.getCategoryList();

        assertNotNull (categoryList);

        Category category = categoryList.get(0);

        assertNotNull (category);

        assertEquals("125", category.getId());
        assertEquals("Trade Balance", category.getName());
        assertEquals("13", category.getParentId());
    }

    /**
     * fred/category/children
     */
    @Test
    public void getCategoryChildren () {

        int categoryId = 13;

        QueryBuilder builder = new QueryBuilder(
            restTemplate,
            "http://api.stlouisfed.org/fred/category/children"
        );

        Categories categories = builder
            .setApiKey(API_KEY)
            .setCategoryId(categoryId)
            .doGet(Categories.class);

        assertNotNull (categories);

        List<Category> categoryList = categories.getCategoryList();

        assertNotNull (categoryList);
        assertEquals(5, categoryList.size());

        Category category = categoryList.get(2);

        assertNotNull (category);

        assertEquals("3000", category.getId());
        assertEquals("Income Payments & Receipts", category.getName());
        assertEquals("13", category.getParentId());
    }

    /**
     * fred/category/related
     */
    @Test
    public void getCategoryRelated () {

        int categoryId = 32073;

        QueryBuilder builder = new QueryBuilder(
            restTemplate,
            "http://api.stlouisfed.org/fred/category/related"
        );

        Categories categories = builder
            .setApiKey(API_KEY)
            .setCategoryId(categoryId)
            .doGet(Categories.class);

        assertNotNull (categories);

        List<Category> categoryList = categories.getCategoryList();

        assertNotNull (categoryList);
        assertEquals(7, categoryList.size());

        Category category = categoryList.get(5);

        assertNotNull (category);

        assertEquals("154", category.getId());
        assertEquals("Missouri", category.getName());
        assertEquals("27281", category.getParentId());
    }

    /**
     * fred/category/series
     */
    @Test
    public void getCategorySeries () {

        int categoryId = 125;

        QueryBuilder builder = new QueryBuilder(
            restTemplate,
            "http://api.stlouisfed.org/fred/category/series"
        );

        Seriess seriess = builder
            .setApiKey(API_KEY)
            .setCategoryId(categoryId)
            .doGet(Seriess.class);

        assertNotNull (seriess);

        List<Series> seriesList = seriess.getSeriesList();

        assertNotNull (seriesList);
        // The example on the FRED website has only 7 results, however if you
        // get the escapedUri you'll see that there are many more returned from
        // this call.
        assertEquals(24, seriesList.size());

        // <series
        //  id="BOPBGSN"
        // realtime_start="2013-09-02"
        // realtime_end="2013-09-02"
        // title="Balance on Goods and Services"
        // observation_start="1960-01-01"
        // observation_end="2013-01-01"
        // frequency="Quarterly"
        // frequency_short="Q"
        // units="Billions of Dollars"
        // units_short="Bil. of $"
        // seasonal_adjustment="Not Seasonally Adjusted"
        // seasonal_adjustment_short="NSA"
        // last_updated="2013-07-09 08:46:24-05"
        // popularity="7"/>

        Series series = seriesList.get(5);

        assertNotNull (series);

        assertEquals("BOPBGSN", series.getId());
        assertDateIsAccurateForToday(series.getRealtimeStart());
        assertDateIsAccurateForToday(series.getRealtimeEnd());
        assertEquals("Balance on Goods and Services", series.getTitle());
        assertDateIsAccurate(
            using (1960, Calendar.JANUARY, 01),
            series.getObservationStart());
        // TODO: Need to determine why the date has changed from JAN to APR.
        assertDateIsAccurate(
            using (2013, Calendar.APRIL, 01),
            series.getObservationEnd());
        assertEquals("Quarterly", series.getFrequency());
        assertEquals("Q", series.getFrequencyShort());
        assertEquals("Billions of Dollars", series.getUnits());
        assertEquals("Bil. of $", series.getUnitsShort());
        assertEquals("Not Seasonally Adjusted", series.getSeasonalAdjustment());
        assertEquals("NSA", series.getSeasonalAdjustmentShort());
        // TODO: Need to determine why the date has changed from JUL 09 to
        //       SEPT 19.
        assertDateIsAccurate(
            using (2013, Calendar.SEPTEMBER, 19),
            series.getLastUpdated());
        // Popularity can change so we're only checking if it's not null.
        assertNotNull(series.getPopularity());

    }

    @Test
    public void getSources () {
        QueryBuilder builder = new QueryBuilder(
            restTemplate,
            "http://api.stlouisfed.org/fred/sources"
        );

        Sources sources = builder
            .setApiKey(API_KEY)
            .doGet(Sources.class);

        assertNotNull (sources);
//        assertDateIsAccurateForToday(sources.getRealtimeStart());
//        assertDateIsAccurateForToday(sources.getRealtimeEnd());
        assertEquals(OrderBy.sourceId, sources.getOrderBy());
        assertEquals(SortOrder.asc, sources.getSortOrder());
        assertTrue(0 < sources.getCount());
        assertEquals(0, sources.getOffset());
        assertEquals(1000, sources.getLimit());

        List<Source> sourceList = sources.getSourceList();

        assertEquals(59, sourceList.size());

        Source source7 = sourceList.get(7);

//        assertDateIsAccurateForToday(source7.getRealtimeStart());
//        assertDateIsAccurateForToday(source7.getRealtimeEnd());
        assertEquals(
            "The White House: Council of Economic Advisors", source7.getName());
        assertEquals("http://www.whitehouse.gov/cea/", source7.getLink());
    }

    @Test
    public void getSource () {
        QueryBuilder builder = new QueryBuilder(
            restTemplate,
            "http://api.stlouisfed.org/fred/source"
        );

        Sources sources = builder
            .setApiKey(API_KEY)
            .setSourceId(1)
            .doGet(Sources.class);

        assertNotNull (sources);
        assertDateIsAccurateForToday(sources.getRealtimeStart());
        assertDateIsAccurateForToday(sources.getRealtimeEnd());

        List<Source> sourceList = sources.getSourceList();

        Source source0 = sourceList.get(0);

        assertDateIsAccurateForToday(source0.getRealtimeStart());
        assertDateIsAccurateForToday(source0.getRealtimeEnd());
        assertEquals(
            "Board of Governors of the Federal Reserve System", source0.getName());
        assertEquals("http://www.federalreserve.gov/", source0.getLink());
    }

    @Test
    public void getSourceRelease () {
        QueryBuilder builder = new QueryBuilder(
            restTemplate,
            "http://api.stlouisfed.org/fred/source/releases"
        );

        Releases releases = builder
            .setApiKey(API_KEY)
            .setSourceId(1)
            .doGet(Releases.class);

//        assertDateIsAccurateForToday(releases.getRealtimeStart());
//        assertDateIsAccurateForToday(releases.getRealtimeEnd());
        assertEquals(OrderBy.releaseId, releases.getOrderBy());
        assertEquals(SortOrder.asc, releases.getSortOrder());
        assertEquals(26, releases.getCount());
        assertEquals(0, releases.getOffset());
        assertEquals(1000, releases.getLimit());

        List<Release> releaseList = releases.getReleaseList();

        Release release = releaseList.get(7);

        assertEquals(Long.valueOf(21L), release.getId());
//        assertDateIsAccurateForToday(release.getRealtimeStart());
//        assertDateIsAccurateForToday(release.getRealtimeEnd());
        assertEquals("H.6 Money Stock Measures", release.getName());
        assertEquals(Boolean.TRUE, release.isPressRelease());
        assertEquals(
            "http://www.federalreserve.gov/releases/h6/", release.getLink());
    }

    @Test
    public void getReleases () {
        QueryBuilder builder = new QueryBuilder(
            restTemplate,
            "http://api.stlouisfed.org/fred/releases"
        );

        Releases releases = builder
            .setApiKey(API_KEY)
            .doGet(Releases.class);

        assertDateIsAccurateForToday(releases.getRealtimeStart());
        assertDateIsAccurateForToday(releases.getRealtimeEnd());
        assertEquals(OrderBy.releaseId, releases.getOrderBy());
        assertEquals(SortOrder.asc, releases.getSortOrder());
        assertTrue(0 < releases.getCount());
        assertEquals(0, releases.getOffset());
        assertEquals(1000, releases.getLimit());

        List<Release> releaseList = releases.getReleaseList();

        Release release = releaseList.get(2);

        assertEquals(Long.valueOf(11L), release.getId());
        assertDateIsAccurateForToday(release.getRealtimeStart());
        assertDateIsAccurateForToday(release.getRealtimeEnd());
        assertEquals("Employment Cost Index", release.getName());
        assertEquals(Boolean.TRUE, release.isPressRelease());
        assertEquals(
            "http://www.bls.gov/ncs/ect/", release.getLink());
    }

    @Test
    public void getReleasesDates () {
        QueryBuilder builder = new QueryBuilder(
            restTemplate,
            "http://api.stlouisfed.org/fred/releases/dates"
        );

        ReleaseDates releaseDates = builder.setApiKey(API_KEY)
            .setRealtimeStart("2012-06-18")
            .setRealtimeEnd("2012-06-18")
            .doGet(ReleaseDates.class);

        List<ReleaseDate> releaseDateList = releaseDates.getReleaseDateList();

        ReleaseDate releaseDate1 = releaseDateList.get(1);

        assertEquals(Long.valueOf("183"), releaseDate1.getReleaseId());
        assertEquals("Weekly Retail Gasoline and Diesel Prices",
            releaseDate1.getReleaseName());

        assertDateIsAccurate(
            using (2012, Calendar.JUNE, 18),
            releaseDate1.getDate());
    }

    @Test
    public void getRelease () {
        QueryBuilder builder = new QueryBuilder(
            restTemplate,
            "http://api.stlouisfed.org/fred/release"
        );

        Releases releases = builder
            .setApiKey(API_KEY)
            .setReleaseId(53L)
            .setRealtimeStart("2008-07-28")
            .setRealtimeEnd("2008-07-28")
            .doGet(Releases.class);

        assertDateIsAccurate(
            using (2008, Calendar.JULY, 28),
            releases.getRealtimeStart());
        assertDateIsAccurate(
            using (2008, Calendar.JULY, 28),
            releases.getRealtimeEnd());

        List<Release> releaseList = releases.getReleaseList();

        Release release = releaseList.get(0);

        assertEquals(Long.valueOf(53L), release.getId());
        assertDateIsAccurate(
            using (2008, Calendar.JULY, 28),
            release.getRealtimeStart());
        assertDateIsAccurate(
            using (2008, Calendar.JULY, 28),
            release.getRealtimeEnd());
        assertEquals("Gross Domestic Product", release.getName());
        assertEquals(Boolean.TRUE, release.isPressRelease());
        assertEquals(
            "http://www.bea.gov/national/index.htm", release.getLink());
    }

    @Test
    public void getReleaseDates () {
        QueryBuilder builder = new QueryBuilder(
            restTemplate,
            "http://api.stlouisfed.org/fred/release/dates"
        );

        ReleaseDates releaseDates = builder.setApiKey(API_KEY)
            .setReleaseId(82L)
            .setRealtimeStart("2010-06-01")
            .setRealtimeEnd("2012-06-18")
            .doGet(ReleaseDates.class);

        assertDateIsAccurate(
            using (2010, Calendar.JUNE, 01),
            releaseDates.getRealtimeStart());
        assertDateIsAccurate(
            using (2012, Calendar.JUNE, 18),
            releaseDates.getRealtimeEnd());
        assertEquals(OrderBy.releaseDate, releaseDates.getOrderBy());
        assertEquals(SortOrder.asc, releaseDates.getSortOrder());
        assertEquals(1, releaseDates.getCount());
        assertEquals(0, releaseDates.getOffset());
        assertEquals(10000, releaseDates.getLimit());

        
        List<ReleaseDate> releaseDateList = releaseDates.getReleaseDateList();

        ReleaseDate releaseDate0 = releaseDateList.get(0);

        assertEquals(Long.valueOf("82"), releaseDate0.getReleaseId());

        assertDateIsAccurate(
            using (2011, Calendar.FEBRUARY, 23),
            releaseDate0.getDate()
        );
    }

    @Test
    public void getReleaseSeries () {

        QueryBuilder builder = new QueryBuilder (
            restTemplate,
            "http://api.stlouisfed.org/fred/release/series");

        Seriess result = builder
            .setApiKey(API_KEY)
            .setReleaseId(51L)
            .setRealtimeStart(realtimeStart)
            .setRealtimeEnd(realtimeEnd)
            .doGet (Seriess.class); 

        Date realtimeStartDate = result.getRealtimeStart();
        Date realtimeEndDate = result.getRealtimeEnd();

        assertDateIsAccurate(realtimeStart, realtimeStartDate);

        assertDateIsAccurate(realtimeEnd, realtimeEndDate);
        assertEquals(OrderBy.seriesId, result.getOrderBy());
        assertEquals(SortOrder.asc, result.getSortOrder());
        assertEquals(21, result.getCount());
        assertEquals(0, result.getOffset());
        assertEquals(1000, result.getLimit());

        List<Series> seriesList = result.getSeriesList();

        assertEquals (21, seriesList.size());

        Series seriesOne = seriesList.get(1);

        assertEquals ("BOPGIMP", seriesOne.getId());
        assertEquals ("Imports of Goods: Balance of Payments Basis",
            seriesOne.getTitle());
        assertDateIsAccurate(
            realtimeStart,
            seriesOne.getRealtimeStart());
        assertDateIsAccurate(
            realtimeEnd,
            seriesOne.getRealtimeEnd());
        assertDateIsAccurate (
            using (1992, Calendar.JANUARY, 01),
            seriesOne.getObservationStart());
        assertDateIsAccurate (
            using (2004, Calendar.MARCH, 01),
            seriesOne.getObservationEnd());
        assertEquals ("Monthly", seriesOne.getFrequency());
    }

    @Test
    public void getReleaseSources () {
        QueryBuilder builder = new QueryBuilder(
            restTemplate,
            "http://api.stlouisfed.org/fred/release/sources"
        );

        Sources sources = builder
            .setApiKey(API_KEY)
            .setReleaseId(51L)
            .setRealtimeStart("2010-06-01")
            .setRealtimeEnd("2012-06-18")
            .doGet(Sources.class);

        assertNotNull (sources);
        assertDateIsAccurate (
            using (2010, Calendar.JUNE, 01),
            sources.getRealtimeStart());
        assertDateIsAccurate (
            using (2012, Calendar.JUNE, 18),
            sources.getRealtimeEnd());

        List<Source> sourceList = sources.getSourceList();

        Source source1 = sourceList.get(1);

        assertDateIsAccurate (
            using (2010, Calendar.JUNE, 01),
            source1.getRealtimeStart());
        assertDateIsAccurate (
            using (2012, Calendar.JUNE, 18),
            source1.getRealtimeEnd());
        assertEquals(
            "U.S. Department of Commerce: Census Bureau", source1.getName());
        assertEquals("http://www.census.gov/", source1.getLink());
    }

    private void reviewTag (Tag expectedTag, Tag actualTag) {
        assertEquals(expectedTag.getCreated(), actualTag.getCreated());
        assertEquals(expectedTag.getGroupId(), actualTag.getGroupId());
        assertEquals(expectedTag.getName(), actualTag.getName());
        assertEquals(expectedTag.getNotes(), actualTag.getNotes());
        assertEquals(expectedTag.getPopularity(), actualTag.getPopularity());
        assertEquals(expectedTag.getSeriesCount(), actualTag.getSeriesCount());
    }

    @Test
    public void getTags () {
        Date realtimeStart = using (2001, Calendar.JANUARY, 20);
        Date realtimeEnd = using (2004, Calendar.MAY, 17);

        QueryBuilder builder = new QueryBuilder(
            restTemplate,
            "http://api.stlouisfed.org/fred"
        );

        Tags tags = builder
            .setApiKey(API_KEY)
            .series()
            .search()
            .tags()
            .setRealtimeStart(realtimeStart)
            .setRealtimeEnd(realtimeEnd)
            .setSeriesSearchText("monetary service index")
            .doGet(Tags.class);

        assertNotNull (tags);

        assertDateIsAccurate (
            realtimeStart, tags.getRealtimeStart());
        assertDateIsAccurate (
            realtimeEnd, tags.getRealtimeEnd());

        assertEquals(OrderBy.seriesCount, tags.getOrderBy());
        assertEquals(SortOrder.desc, tags.getSortOrder());
        assertEquals(10, tags.getCount());
        assertEquals(0, tags.getOffset());
        assertEquals(1000, tags.getLimit());

        List<Tag> tagList = tags.getTagList();

        assertNotNull (tagList);

        int size = tagList.size();

        assertEquals(10, size);

        Tag expectedTag = new Tag ();

        expectedTag.setName("anderson & jones");
        expectedTag.setGroupId("src");
        expectedTag.setNotes("Richard Anderson and Barry Jones");
        expectedTag.setCreated("2013-06-21 10:22:49-05");
        expectedTag.setPopularity(45L);
        expectedTag.setSeriesCount(1L);

        Tag actualTag = tagList.get(1);

        reviewTag (expectedTag, actualTag);
    }
}
