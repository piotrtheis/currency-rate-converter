package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;

import java.util.Currency;
import java.util.Optional;
import java.util.function.Predicate;

public class FindAccountAndConvertCurrencyIfPossibleUseCase {

    private final FindAccountUseCase findAccountUseCase;
    private final FindAccountAndConvertCurrencyUseCase findAndConvertUseCase;

    public FindAccountAndConvertCurrencyIfPossibleUseCase(FindAccountUseCase findAccountUseCase, FindAccountAndConvertCurrencyUseCase findAndConvertUseCase) {
        this.findAccountUseCase = findAccountUseCase;
        this.findAndConvertUseCase = findAndConvertUseCase;
    }

    public Optional<Account> execute(Account.Id id, String targetCurrency) {
        return Optional.ofNullable(targetCurrency)
                .filter(Predicate.not(String::isBlank))
                .map(s ->
                        findAndConvertUseCase.execute(id, Currency.getInstance(s))
                )
                .orElseGet(() ->
                        findAccountUseCase.execute(id)
                );
    }

    public Optional<Account> execute(Account.Number number, String targetCurrency) {
        return Optional.ofNullable(targetCurrency)
                .filter(Predicate.not(String::isBlank))
                .map(s ->
                        findAndConvertUseCase.execute(number, Currency.getInstance(s))
                )
                .orElseGet(() ->
                        findAccountUseCase.execute(number)
                );
    }
}
