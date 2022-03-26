package pl.cleankod.exchange.core.domain;

import pl.cleankod.util.Preconditions;

import java.util.Currency;

/**
 * Currency pair like EUR/PLN where base is equal to EUR.
 */
public record CurrencyPair(Currency base, Currency counter) {

    public CurrencyPair {
        Preconditions.requireNonNull(base);
        Preconditions.requireNonNull(counter);

        if (base.equals(counter)) {
            throw new IllegalArgumentException("Wrong pair. The base cannot reference counter");
        }
    }

    public boolean isBase(Currency currency) {
        return base.equals(currency);
    }

    public boolean isCounter(Currency currency) {
        return counter.equals(currency);
    }

    public boolean contains(Currency currency) {
        return base.equals(currency) || counter.equals(currency);
    }
}
