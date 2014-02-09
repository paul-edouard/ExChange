package com.coherentlogic.fred.client.example;

import org.springframework.web.client.RestTemplate;

import com.coherentlogic.fred.client.core.builders.QueryBuilder;
import com.coherentlogic.fred.client.core.factories.QueryBuilderFactory;

/**
 * A RequestBuilderFactory specifically for the series service, which is
 * available at the following URL:
 *
 * http://api.stlouisfed.org/fred/series
 *
 * Whenever the developer invokes the {@link #getInstance()} method an instance
 * of RequestBuilder will be returned with the API key preset to the value which
 * has been passed to the constructor when this class was instantiated.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class SeriesRequestBuilderFactory extends QueryBuilderFactory {

    private final String apiKey;

    public SeriesRequestBuilderFactory(
        RestTemplate restTemplate,
        String uri,
        String apiKey
        ) {
        super(restTemplate, uri, apiKey);
        this.apiKey = apiKey;
    }

    @Override
    public QueryBuilder getInstance() {

        QueryBuilder result = super.getInstance();

        result.setApiKey(apiKey);

        return result;
    }
}
