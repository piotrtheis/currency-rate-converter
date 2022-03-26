package pl.cleankod.exchange.core.domain;

import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
import java.util.Currency;

public record CurrencyRate(CurrencyPair pair, BigDecimal value) {

    public CurrencyRate {
        Preconditions.requireNonNull(pair);
        Preconditions.requireNonNull(value);

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Only positive values  greater than zero allowed");
        }
    }

    /**
     * Check whenever the quote rate can be used to exchange given money.
     *
     * @param money Money you would like to exchange
     * @return true if so
     */
    public boolean isApplicableTo(Money money) {
        var currency = money.currency();
        return pair.contains(currency) && pair.isCounter(currency);
    }

    /**
     * Get target currency to which exchange may happen
     */
    public Currency getTarget() {
        return pair.base();
    }
}
