package pl.cleankod.util.cache;

import java.util.function.Function;

public interface Cache<K, V> {

    V getOrPut(K key, Function<K, V> mappingFunction);
}
