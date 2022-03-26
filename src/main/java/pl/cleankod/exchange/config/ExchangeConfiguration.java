package pl.cleankod.exchange.config;

import feign.Logger;
import feign.httpclient.ApacheHttpClient;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import pl.cleankod.exchange.core.domain.CurrencyPair;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.gateway.CurrencyRateProvider;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyIfPossibleUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.entrypoint.ExceptionHandlerAdvice;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.NbpCurrencyRateProvider;
import pl.cleankod.exchange.provider.nbp.CachedExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;
import pl.cleankod.util.cache.Cache;
import pl.cleankod.util.cache.InMemoryCache;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Currency;

@Configuration
public class ExchangeConfiguration {

    @Bean
    AccountRepository accountRepository() {
        return new AccountInMemoryRepository();
    }

    @Bean
    ExchangeRatesNbpClient exchangeRatesNbpClient(Environment environment) {
        String nbpApiBaseUrl = environment.getRequiredProperty("provider.nbp-api.base-url");
        return HystrixFeign.builder()
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logLevel(Logger.Level.FULL)
                .logger(new Slf4jLogger(ExchangeRatesNbpClient.class))
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
    }

    @Primary
    @Bean
    ExchangeRatesNbpClient cachedExchangeClient(ExchangeRatesNbpClient nbpClient, Cache<String, RateWrapper> cache) {
        return new CachedExchangeRatesNbpClient(nbpClient, cache);
    }

    @Bean
    Cache<String, RateWrapper> nbpCache(Environment environment) {
        Duration ttl = environment.getProperty("provider.nbp-api.cache.ttl", Duration.class, Duration.ofMillis(15));
        return new InMemoryCache<>(ttl);
    }

    @Bean
    Cache<CurrencyPair, BigDecimal> nbpPairCache(Environment environment) {
        Duration ttl = environment.getProperty("provider.nbp-api.cache.ttl", Duration.class, Duration.ofMillis(15));
        return new InMemoryCache<>(ttl);
    }

    @Bean
    CurrencyConversionService currencyConversionService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        return new CurrencyConversionNbpService(exchangeRatesNbpClient);
    }

    @Bean
    FindAccountUseCase findAccountUseCase(AccountRepository accountRepository) {
        return new FindAccountUseCase(accountRepository);
    }

    @Bean
    CurrencyRateProvider currencyRateProvider(ExchangeRatesNbpClient exchangeRatesNbpClient, Cache<CurrencyPair, BigDecimal> cache) {
        return new NbpCurrencyRateProvider(exchangeRatesNbpClient, cache);
    }

    @Bean
    FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase(
            AccountRepository accountRepository,
            CurrencyConversionService currencyConversionService,
            CurrencyRateProvider currencyRateProvider,
            Environment environment
    ) {
        Currency baseCurrency = Currency.getInstance(environment.getRequiredProperty("app.base-currency"));
        return new FindAccountAndConvertCurrencyUseCase(accountRepository, currencyConversionService, currencyRateProvider, baseCurrency);
    }

    @Bean
    FindAccountAndConvertCurrencyIfPossibleUseCase findAccountAndConvertCurrencyIfPossibleUseCase(
            FindAccountUseCase findAccountUseCase,
            FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase
    ) {
        return new FindAccountAndConvertCurrencyIfPossibleUseCase(
                findAccountUseCase,
                findAccountAndConvertCurrencyUseCase
        );
    }

    @Bean
    AccountController accountController(FindAccountAndConvertCurrencyIfPossibleUseCase findAndConvertUseCase) {
        return new AccountController(findAndConvertUseCase);
    }

    @Bean
    ExceptionHandlerAdvice exceptionHandlerAdvice() {
        return new ExceptionHandlerAdvice();
    }
}
