package com.coherentlogic.coherent.data.model.core.builders;

import java.net.URI;


import javax.ws.rs.core.UriBuilder;

import org.springframework.web.client.RestTemplate;

import com.coherentlogic.coherent.data.model.core.cache.CacheServiceProviderSpecification;
import com.coherentlogic.coherent.data.model.core.cache.NullCache;

/**
 * This class acts as the foundation for QueryBuilder implementations. This
 * class has caching capabilities where the key is the URI and the value is an
 * instance of a domain class.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public abstract class AbstractQueryBuilder
    implements HttpMethodsSpecification {

    private final RestTemplate restTemplate;

    private final UriBuilder uriBuilder;

    private final CacheServiceProviderSpecification<String, Object> cache;

    protected static final NullCache NULL_CACHE = new NullCache();

    static UriBuilder newUriBuilder (String uri) {
        UriBuilder builder;

        builder = UriBuilder.fromPath(uri);

        return builder;
    }

    protected AbstractQueryBuilder (
        RestTemplate restTemplate,
        String uri
    ) {
        this (restTemplate, newUriBuilder (uri));
    }

    protected AbstractQueryBuilder (
        RestTemplate restTemplate,
        UriBuilder uriBuilder
    ) {
        this (restTemplate, uriBuilder, NULL_CACHE);
    }

    protected AbstractQueryBuilder (
        RestTemplate restTemplate,
        String uri,
        CacheServiceProviderSpecification<String, Object> cache
    ) {
        this (restTemplate, newUriBuilder (uri), cache);
    }

    protected AbstractQueryBuilder (
        RestTemplate restTemplate,
        UriBuilder uriBuilder,
        CacheServiceProviderSpecification<String, Object> cache
    ) {
        this.restTemplate = restTemplate;
        this.uriBuilder = uriBuilder;
        this.cache = cache;
    }

    /**
     * Method adds a name-value pair to the internal list of name-value pairs.
     *
     * @param name The name of the parameter.
     * @param value The parameter value.
     *
     * @throws IllegaStateException If either the name or value is null.
     */
    protected void addParameter (String name, String value) {

        // The uriBuilder will throw an exception if the name is null. We add
        // an additional check so that an exception is thrown if the value is
        // null. The reason for this is that the parameter should not be added
        // unless there's an appropriate value.
        if (name == null || value == null)
            throw new IllegalArgumentException("The name and value must " +
                "both be set to non-null values (name: " + name + ", value: " +
                value + ").");

        uriBuilder.queryParam(name, value);//addParameter(name, value);
    }

    /**
     * Method extends the {@link #uriBuilder}'s path with the path value -- ie.
     * 
     * http://www.foo.bar/ becomes http://www.foo.bar/baz/.
     * 
     * @param path The additional path value -- in the example above, 'baz'.
     */
    protected AbstractQueryBuilder extendPathWith (String path) {
        uriBuilder.path(path);

        return this;
    }

    /**
     * Getter method for the cache service provider, which may be null if one
     * was not set.
     */
    protected CacheServiceProviderSpecification<String, Object>
        getCacheServiceProvider () {
        return cache;
    }

    /**
     * Method returns the escaped URI that is actually send to the World Bank
     * web service when the execute method has been called.
     *
     * This method can be useful when debugging, just print the escaped URI and
     * paste it into the address bar in your browser and see what's returned.
     *
     * @return A sting representation of the escaped URI.
     *
     * @todo This method needs to go in a parent class.
     */
    public String getEscapedURI () {

        URI uri = uriBuilder.build();

        String escapedURI = uri.toASCIIString();

        return escapedURI;
    }

    /**
     * Getter method for the RestTemplate.
     */
    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    /**
     * Getter method for the RestTemplate.
     */
    protected UriBuilder getUriBuilder() {
        return uriBuilder;
    }

    /**
     * Method constructs the URI and first checks to see if the object currently
     * exists in the cache -- if it does, then this object is returned, other-
     * -wise the URI is called and the resultant XML is converted into an
     * instance of type <i>type</i> and the result is returned to the user. 
     */
    public <T> T doGet (Class<T> type) {

        String escapedURI = getEscapedURI();

        T result = null;

        Object object = cache.get(escapedURI);

        if (object != null && type.isInstance(object))
            result = (T) object;
        else if (object != null && !type.isInstance(object))
            throw new ClassCastException (
                "The object " + object +
                " cannot be cast to type " + type + ".");
        else if (object == null) {
            result = (T) restTemplate.getForObject(escapedURI, type);
            cache.put(escapedURI, result);
        }

        return result;
    }
}
