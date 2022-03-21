package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.util.domain.Result;

import java.util.Currency;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FindAccountAndConvertCurrencyIfPossibleUseCase {

    private final FindAccountUseCase findAccountUseCase;
    private final FindAccountAndConvertCurrencyUseCase findAndConvertUseCase;

    public FindAccountAndConvertCurrencyIfPossibleUseCase(FindAccountUseCase findAccountUseCase, FindAccountAndConvertCurrencyUseCase findAndConvertUseCase) {
        this.findAccountUseCase = findAccountUseCase;
        this.findAndConvertUseCase = findAndConvertUseCase;
    }

    public Result<Account, FailedReason> execute(Account.Id id, String targetCurrency) {
        var currency = getCurrentInstance(targetCurrency);

        if (currency.isEmpty()) {
            return find(id);
        }

        return convert(id, currency.get());
    }

    public Result<Account, FailedReason> execute(Account.Number number, String targetCurrency) {
        var currency = getCurrentInstance(targetCurrency);

        if (currency.isEmpty()) {
            return find(number);
        }

        return convert(number, currency.get());
    }

    private Result<Account, FailedReason> find(Account.Id id) {
        return findAccountUseCase.execute(id)
                .map(Result::<Account, FailedReason>successful)
                .orElseGet(() -> Result.fail(FailedReason.MISSING_ACCOUNT));
    }

    private Result<Account, FailedReason> find(Account.Number number) {
        return findAccountUseCase.execute(number)
                .map(Result::<Account, FailedReason>successful)
                .orElseGet(() -> Result.fail(FailedReason.MISSING_ACCOUNT));
    }

    private Result<Account, FailedReason> convert(Account.Id id, Currency currency) {
        return handleErrors(() -> findAndConvertUseCase.execute(id, currency)
                .map(Result::<Account, FailedReason>successful)
                .orElseGet(() -> Result.fail(FailedReason.MISSING_ACCOUNT))
        );
    }

    private Result<Account, FailedReason> convert(Account.Number number, Currency currency) {
        return handleErrors(() -> findAndConvertUseCase.execute(number, currency)
                    .map(Result::<Account, FailedReason>successful)
                    .orElseGet(() -> Result.fail(FailedReason.MISSING_ACCOUNT))
        );
    }

    private Result<Account, FailedReason> handleErrors(Supplier<Result<Account, FailedReason>> supplier) {
        try {
            return supplier.get();
        } catch (CurrencyConversionException e) {
            return Result.fail(FailedReason.UNSUPPORTED_CURRENCY);
        } catch (RuntimeException e) {
            return Result.fail(FailedReason.UNKNOWN);
        }
    }

    private Optional<Currency> getCurrentInstance(String targetCurrency) {
        return Optional.ofNullable(targetCurrency)
                .filter(Predicate.not(String::isBlank))
                .map(Currency::getInstance);
    }

    public static enum FailedReason {
        MISSING_ACCOUNT,
        UNSUPPORTED_CURRENCY,
        UNKNOWN
    }
}
