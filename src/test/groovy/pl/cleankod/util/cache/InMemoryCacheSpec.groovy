package pl.cleankod.util.cache

import spock.lang.Specification

import java.time.Duration
import java.util.concurrent.TimeUnit

class InMemoryCacheSpec extends Specification {

    def "valid entry should be present"() {
        given:
        def val1 = new String("value")
        def val2 = new String("value")
        def cache = new InMemoryCache<String, String>(Duration.ofHours(1))
        cache.getOrPut("key", s -> {
            return val1
        })

        when:
        def entry = cache.getOrPut("key", s -> {
            return val2
        })

        then:
        entry === val1
    }

    def "expired entry should not be present"() {
        given:
        def cache = new InMemoryCache<String, String>(Duration.ofNanos(1))
        cache.getOrPut("key", s -> {
            return "value"
        })

        when:
        TimeUnit.NANOSECONDS.sleep(2)
        def entry = cache.getOrPut("key", s -> {
            return null
        })

        then:
        entry == null
    }

}
