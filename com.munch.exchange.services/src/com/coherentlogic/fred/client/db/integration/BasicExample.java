package com.coherentlogic.fred.client.db.integration;

import java.util.Calendar;
import java.util.Date;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.coherentlogic.fred.client.core.builders.QueryBuilder;
import com.coherentlogic.fred.client.core.domain.FilterValue;
import com.coherentlogic.fred.client.core.domain.FilterVariable;
import com.coherentlogic.fred.client.core.domain.OrderBy;
import com.coherentlogic.fred.client.core.domain.SearchType;
import com.coherentlogic.fred.client.core.domain.Seriess;
import com.coherentlogic.fred.client.core.domain.SortOrder;
import com.coherentlogic.fred.client.db.integration.dao.SeriessDAO;

/**
 * An example that queries the FRED servers for a seriess object and then saves
 * the result to an instance of the H2 database running in memory.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public class BasicExample {

    private static final String DEFAULT_APP_CTX_PATH =
        "spring/db/application-context.xml", BASIC_EXAMPLE = "basicExample";

    private final QueryBuilder seriessRequestBuilder;

    private final SeriessDAO seriessDAO;

    public BasicExample(
        QueryBuilder seriessRequestBuilder,
        SeriessDAO seriessDAO
    ) {
        super();
        this.seriessRequestBuilder = seriessRequestBuilder;
        this.seriessDAO = seriessDAO;
    }

    static Date getDate (int year, int month, int day) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        return calendar.getTime();
    }

    public Seriess querySeriess () {
        Date realtimeStart = getDate (2001, Calendar.JANUARY, 20);
        Date realtimeEnd = getDate (2004, Calendar.MAY, 17);

        Seriess result = seriessRequestBuilder
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

        return result;
    }

    public void save (Seriess seriess) {
        seriessDAO.persist(seriess);
    }

    public static void main (String[] unused) {

        AbstractApplicationContext applicationContext =
            new ClassPathXmlApplicationContext (DEFAULT_APP_CTX_PATH);

        BasicExample basicExample =
            (BasicExample) applicationContext.getBean(BASIC_EXAMPLE);

        Seriess seriess = basicExample.querySeriess();

        basicExample.save(seriess);
    }
}
