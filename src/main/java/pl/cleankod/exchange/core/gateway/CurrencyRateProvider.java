package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.CurrencyPair;
import pl.cleankod.exchange.core.domain.CurrencyRate;

import java.util.Optional;

public interface CurrencyRateProvider {

    Optional<CurrencyRate> getRate(CurrencyPair pair);
}
