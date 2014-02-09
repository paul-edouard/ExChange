package com.coherentlogic.coherent.data.model.core.cache;

import java.util.Map;

/**
 * Use this class with cache providers that implement the java.util.Map
 * interface (JBoss Infinispan and Oracle Coherence are two examples).
 *
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */
public class MapCompliantCacheServiceProvider<K, V>
    implements CacheServiceProviderSpecification<K, V> {

    private final Map<K, V> cache;

    public MapCompliantCacheServiceProvider(Map<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }
}
