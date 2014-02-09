package com.coherentlogic.coherent.data.model.core.cache;


/**
 * A cache service provider implementation which is used when no caching is
 * configured.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public class NullCache
    implements CacheServiceProviderSpecification<String, Object> {

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public void put(String key, Object value) {
    }
}
