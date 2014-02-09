package com.coherentlogic.coherent.data.model.core.factories;

import org.springframework.web.client.RestTemplate;

import com.coherentlogic.coherent.data.model.core.builders.HttpMethodsSpecification;

/**
 * A factory which is used for creating instances of AbstractQueryBuilder.
 * <p>
 * Since the AbstractQueryBuilder is not thread-safe, it cannot be set as a member
 * variable -- instead, use this factory and call {@link #getInstance()}
 * whenever you need to query the FRED web services.
 * <p>
 * This class can be extended, for example, if you need to always return the
 * same value for some query parameter -- for example, in this class we always
 * return the same API key.
 * <p>
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public abstract class AbstractQueryBuilderFactory
    <T extends HttpMethodsSpecification> implements Factory<T> {

    private final RestTemplate restTemplate;

    private final String uri;

    public AbstractQueryBuilderFactory (
        RestTemplate restTemplate,
        String uri
    ) {
        this.restTemplate = restTemplate;
        this.uri = uri;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public String getUri() {
        return uri;
    }
}
