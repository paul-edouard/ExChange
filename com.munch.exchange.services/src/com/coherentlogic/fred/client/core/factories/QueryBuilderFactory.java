package com.coherentlogic.fred.client.core.factories;

import org.springframework.web.client.RestTemplate;

import com.coherentlogic.coherent.data.model.core.cache.CacheServiceProviderSpecification;
import com.coherentlogic.fred.client.core.builders.QueryBuilder;

/**
 * A factory which is used for creating instances of QueryBuilder.
 * <p>
 * Since the QueryBuilder is not thread-safe, it cannot be set as a member
 * variable -- instead, use this factory and call {@link #getInstance()}
 * whenever you need to query the FRED web services.
 * <p>
 * This class can be extended, for example, if you need to always return the
 * same value for some query parameter -- for example, in this class we always
 * return the same API key.
 * <p>
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class QueryBuilderFactory
    implements Factory<QueryBuilder> {

    private final RestTemplate restTemplate;

    private final String uri;

    private final CacheServiceProviderSpecification<String, Object>
        cacheServiceProvider;

    private final String apiKey;

    public QueryBuilderFactory (
        RestTemplate restTemplate,
        String uri,
        String apiKey
    ) {
        this (restTemplate, uri, null, apiKey);
    }

    public QueryBuilderFactory (
        RestTemplate restTemplate,
        String uri,
        CacheServiceProviderSpecification<String, Object> cacheServiceProvider,
        String apiKey
    ) {
        this.restTemplate = restTemplate;
        this.uri = uri;
        this.cacheServiceProvider = cacheServiceProvider;
        this.apiKey = apiKey;
    }

    @Override
    public QueryBuilder getInstance() {

        QueryBuilder result = null;

        if (cacheServiceProvider == null )
            result = new QueryBuilder (restTemplate, uri);
        else
            result = new QueryBuilder (restTemplate, uri, cacheServiceProvider);

        result.setApiKey(apiKey);

        return result;
    }
}
