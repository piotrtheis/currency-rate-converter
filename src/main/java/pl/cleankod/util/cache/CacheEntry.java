package pl.cleankod.util.cache;

import java.time.Duration;
import java.time.Instant;

public class CacheEntry<V> {
    private final V value;
    private final Instant expiration;

    public CacheEntry(V value, Duration ttl) {
        this.value = value;
        this.expiration = Instant.now().plus(ttl);
    }

    public boolean isValid() {
        return Instant.now().isBefore(expiration);
    }

    public V getValue() {
        return value;
    }
}