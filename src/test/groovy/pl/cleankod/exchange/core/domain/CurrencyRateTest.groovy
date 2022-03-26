package pl.cleankod.exchange.core.domain

import spock.lang.Specification

class CurrencyRateTest extends Specification {

    private static final eurPlnPair = new CurrencyPair(
            Currency.getInstance("EUR"),
            Currency.getInstance("PLN")
    )

    private static final plnEurPair = new CurrencyPair(
            Currency.getInstance("PLN"),
            Currency.getInstance("EUR")
    )

    def "should create object for valid values"() {
        when:
        def rate = new CurrencyRate(eurPlnPair, BigDecimal.ONE)

        then:
        rate.getTarget() == Currency.getInstance("EUR")
        rate.value() == BigDecimal.ONE
    }

    def "should not create object with value less or equal zero"() {
        when:
        new CurrencyRate(eurPlnPair, rateValue)

        then:
        thrown(IllegalArgumentException)

        where:
        rateValue               | _
        BigDecimal.ZERO         | _
        BigDecimal.ONE.negate() | _
        new BigDecimal("-0.1")  | _
    }

    def "should not consider the same pairs for trading"() {
        expect:
        rate.isApplicableTo(money) == expectedRezult

        where:
        rate                                         | money                  || expectedRezult
        new CurrencyRate(eurPlnPair, BigDecimal.ONE) | Money.of("100", "PLN") || true
        new CurrencyRate(eurPlnPair, BigDecimal.ONE) | Money.of("100", "EUR") || false
        new CurrencyRate(plnEurPair, BigDecimal.ONE) | Money.of("100", "PLN") || false
        new CurrencyRate(plnEurPair, BigDecimal.ONE) | Money.of("100", "EUR") || true
        new CurrencyRate(eurPlnPair, BigDecimal.ONE) | Money.of("100", "USD") || false
        new CurrencyRate(plnEurPair, BigDecimal.ONE) | Money.of("100", "USD") || false
    }
}
