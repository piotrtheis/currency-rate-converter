package pl.cleankod.exchange.core.domain

import spock.lang.Specification

class CurrencyPairTest extends Specification {

    def "should create object for valid values using factory method"() {
        when:
        def pair = new CurrencyPair(base, counter)

        then:
        pair.isBase(base)
        pair.isCounter(counter)

        where:
        base  | counter
        Currency.getInstance("PLN") | Currency.getInstance("EUR")
        Currency.getInstance("EUR") | Currency.getInstance("PLN")
    }

    def "should not create object for the same pairs"() {
        when:
        new CurrencyPair(base, counter)

        then:
        thrown(IllegalArgumentException)

        where:
        base  | counter
        Currency.getInstance("PLN") | Currency.getInstance("PLN")
        Currency.getInstance("EUR") | Currency.getInstance("EUR")
    }
}
