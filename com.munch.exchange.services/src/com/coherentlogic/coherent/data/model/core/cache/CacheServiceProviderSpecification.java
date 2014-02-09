package com.coherentlogic.coherent.data.model.core.cache;

/**
 * An interface that can be implemented such that calls to any Cache
 * implementation can be performed.
 *
 * Note that since JSR-107 is not complete at this time, which is why we're not
 * using it in this framework.
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */
public interface CacheServiceProviderSpecification<K, V> {

    /**
     * Method returns the value associated with the given key.
     */
    V get (K key);

    /**
     * Method add the key and value to the cache.
     */
    void put (K key, V value);
}
