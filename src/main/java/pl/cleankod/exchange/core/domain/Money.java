package pl.cleankod.exchange.core.domain;

import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record Money(BigDecimal amount, Currency currency) {

    private static final int PRESENTATION_PRECISION = 2;
    private static final int CALCULATION_PRECISION = 4;
    private static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    public Money {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money of(String amount, String currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);

        return new Money(new BigDecimal(amount), Currency.getInstance(currency));
    }

    private static Money of(Money money, Currency currency) {
        return new Money(money.amount, currency);
    }

    public Money convert(CurrencyConversionService currencyConverter, Currency targetCurrency) {
        return currencyConverter.convert(this, targetCurrency);
    }

    public Money divideBy(BigDecimal divisor) {
        return new Money(amount.divide(divisor, CALCULATION_PRECISION, DEFAULT_ROUNDING_MODE), currency);
    }

    public Money exchange(CurrencyRate rate) {
        if (!rate.isApplicableTo(this)) {
            return this;
        }

        return Money.of(divideBy(rate.value()), rate.getTarget());
    }

    public String currencyCode() {
        return currency.getCurrencyCode();
    }

    public BigDecimal amount() {
        return amount(PRESENTATION_PRECISION);
    }

    public BigDecimal amount(int precision) {
        return amount.setScale(precision, RoundingMode.HALF_UP);
    }
}
