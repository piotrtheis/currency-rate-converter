package pl.cleankod.exchange.provider.nbp;

import pl.cleankod.exchange.provider.nbp.model.RateWrapper;
import pl.cleankod.util.cache.Cache;

public class CachedExchangeRatesNbpClient implements ExchangeRatesNbpClient {

    private final ExchangeRatesNbpClient client;
    private final Cache<String, RateWrapper> cache;

    public CachedExchangeRatesNbpClient(ExchangeRatesNbpClient client, Cache<String, RateWrapper> cache) {
        this.client = client;
        this.cache = cache;
    }

    public RateWrapper fetch(String table, String currency) {
        var key = getKey(table, currency);

        return cache.getOrPut(key,s -> client.fetch(table, currency));
    }

    private String getKey(String table, String currency) {
        return "%s:%s".formatted(table, currency);
    }
}
