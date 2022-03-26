package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.CurrencyPair;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.gateway.CurrencyRateProvider;

import java.util.Currency;
import java.util.Optional;

public class FindAccountAndConvertCurrencyUseCase {

    private final AccountRepository accountRepository;
    private final CurrencyConversionService currencyConversionService;
    private CurrencyRateProvider currencyRateProvider;
    private final Currency baseCurrency;

    public FindAccountAndConvertCurrencyUseCase(AccountRepository accountRepository,
                                                CurrencyConversionService currencyConversionService,
                                                CurrencyRateProvider currencyRateProvider,
                                                Currency baseCurrency) {
        this.accountRepository = accountRepository;
        this.currencyConversionService = currencyConversionService;
        this.currencyRateProvider = currencyRateProvider;
        this.baseCurrency = baseCurrency;
    }

    public Optional<Account> execute(Account.Id id, Currency targetCurrency) {
        return accountRepository.find(id)
                .map(account -> new Account(account.id(), account.number(), convert(account.balance(), targetCurrency)));
    }

    public Optional<Account> execute(Account.Number number, Currency targetCurrency) {
        return accountRepository.find(number)
                .map(account -> new Account(account.id(), account.number(), convert(account.balance(), targetCurrency)));
    }

    private Money convert(Money money, Currency targetCurrency) {
        if (money.currency().equals(targetCurrency)) {
            return money;
        }

        if (!baseCurrency.equals(targetCurrency)) {
            var pair = new CurrencyPair(targetCurrency, money.currency());


            return currencyRateProvider.getRate(pair).map(money::exchange).orElseThrow(() -> {
                throw new CurrencyConversionException(money.currency(), targetCurrency);
            });
        }

        throw new CurrencyConversionException(money.currency(), targetCurrency);
    }
}
