package pl.cleankod.exchange.entrypoint;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get account by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account details", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AccountViewModel.class))}),
            @ApiResponse(responseCode = "400", description = "Validation error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "404", description = "Missing account", content = @Content),
    })
    @Get("/{id}")
    public HttpResponse<?> findAccountById(String id, @Nullable @QueryValue String currency) {
        return findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id), currency)
                .fold(account -> HttpResponse.ok(AccountViewModel.from(account)), this::errorHandler);
    }

    @Operation(summary = "Get account by number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account details", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AccountViewModel.class))}),
            @ApiResponse(responseCode = "400", description = "Validation error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "404", description = "Missing account", content = @Content),
    })
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
