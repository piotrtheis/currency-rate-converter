package pl.cleankod.exchange.provider;

import lombok.extern.slf4j.Slf4j;
import pl.cleankod.exchange.core.domain.CurrencyPair;
import pl.cleankod.exchange.core.domain.CurrencyRate;
import pl.cleankod.exchange.core.gateway.CurrencyRateProvider;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;
import pl.cleankod.util.cache.Cache;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

@Slf4j
public class NbpCurrencyRateProvider implements CurrencyRateProvider {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final Cache<CurrencyPair, BigDecimal> cache;

    public NbpCurrencyRateProvider(ExchangeRatesNbpClient exchangeRatesNbpClient, Cache<CurrencyPair, BigDecimal> cache) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.cache = cache;
    }

    @Override
    public Optional<CurrencyRate> getRate(CurrencyPair pair) {
        BigDecimal midRate;

        try {
            RateWrapper rateWrapper = getRawValue(pair.base());
            var rate = rateWrapper.rates().get(0).mid();
            midRate = rate;
            cache.getOrPut(pair, p -> rate);
        } catch (RuntimeException e) {
            log.error("Currency rate provider had a bad time", e);

            midRate = cache.getOrPut(pair, p -> null);
        }

        return Optional.ofNullable(midRate).map(v -> new CurrencyRate(pair, v));
    }

    private RateWrapper getRawValue(Currency targetCurrency) {
        return exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
    }
}
