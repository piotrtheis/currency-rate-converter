package pl.cleankod.util.cache;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class InMemoryCache<K, V> implements Cache<K, V> {
    private final Map<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final Duration ttl;

    public InMemoryCache(Duration ttl) {
        this.ttl = ttl;
    }

    @Override
    public V getOrPut(K key, Function<K, V> mappingFunction) {
        var entry = cache.get(key);

        if (entry != null && entry.isValid()) {
            return entry.getValue();
        }

        V computedValue = mappingFunction.apply(key);
        cache.put(key, new CacheEntry<>(computedValue, ttl));

        return computedValue;
    }
}
