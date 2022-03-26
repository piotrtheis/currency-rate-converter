package pl.cleankod.exchange.provider;

public class CurrencyRateUnavailableException extends RuntimeException {

    public CurrencyRateUnavailableException(String message) {
        super(message);
    }
}