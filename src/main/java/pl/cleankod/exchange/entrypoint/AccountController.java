package pl.cleankod.exchange.entrypoint;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyIfPossibleUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyIfPossibleUseCase.FailedReason;
import pl.cleankod.exchange.entrypoint.model.AccountViewModel;
import pl.cleankod.exchange.entrypoint.model.ApiError;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Controller("/accounts")
public class AccountController {

    private final FindAccountAndConvertCurrencyIfPossibleUseCase findAccountAndConvertCurrencyUseCase;

    public AccountController(FindAccountAndConvertCurrencyIfPossibleUseCase findAccountAndConvertCurrencyUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
    }

    @Get("/{id}")
    public HttpResponse<?> findAccountById(String id, @Nullable @QueryValue String currency) {
        return findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id), currency)
                .fold(account -> HttpResponse.ok(AccountViewModel.from(account)), this::errorHandler);
    }

    @Get("/number={+number}")
    public HttpResponse<?> findAccountByNumber(String number, @Nullable @QueryValue String currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return findAccountAndConvertCurrencyUseCase.execute(accountNumber, currency)
                .fold(account -> HttpResponse.ok(AccountViewModel.from(account)), this::errorHandler);
    }

    private HttpResponse<ApiError> errorHandler(FailedReason failedReason) {
        return switch (failedReason) {
            case MISSING_ACCOUNT -> HttpResponse.notFound();
            case UNKNOWN -> HttpResponse.badRequest();
            case UNSUPPORTED_CURRENCY -> HttpResponse
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiError("Cannot convert currency from EUR to PLN."));
        };
    }
}
