package com.coherentlogic.fred.client.example;

import com.coherentlogic.fred.client.core.builders.QueryBuilder;
import com.coherentlogic.fred.client.core.domain.Seriess;
import com.coherentlogic.fred.client.core.factories.Factory;

/**
 * The SeriesService has one method available, which the user can call to query
 * the FRED web service. The API key is set in the Spring application context
 * file, and the queryBuilderFactory will set this value in advance.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class SeriesService {

    private final Factory<QueryBuilder> queryBuilderFactory;

    public SeriesService(Factory<QueryBuilder> queryBuilderFactory) {
        this.queryBuilderFactory = queryBuilderFactory;
    }

    public Seriess getSeries (
        String seriesId,
        String realtimeStartDate,
        String realtimeEndDate
    ) {

        QueryBuilder builder = queryBuilderFactory.getInstance();

        Seriess result = builder
            .setSeriesId(seriesId)
            .setRealtimeStart(realtimeStartDate)
            .setRealtimeEnd(realtimeEndDate)
            .doGet(Seriess.class);

        return result;
    }
}
