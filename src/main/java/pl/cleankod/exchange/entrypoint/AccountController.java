package pl.cleankod.exchange.entrypoint;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyIfPossibleUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyIfPossibleUseCase.FailedReason;
import pl.cleankod.exchange.entrypoint.model.ApiError;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final FindAccountAndConvertCurrencyIfPossibleUseCase findAccountAndConvertCurrencyUseCase;

    public AccountController(FindAccountAndConvertCurrencyIfPossibleUseCase findAccountAndConvertCurrencyUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
        return findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id), currency)
                .fold(ResponseEntity::ok, this::errorHandler);
    }

    @GetMapping(path = "/number={number}")
    public ResponseEntity<?> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return findAccountAndConvertCurrencyUseCase.execute(accountNumber, currency)
                .fold(ResponseEntity::ok, this::errorHandler);
    }


    private ResponseEntity<ApiError> errorHandler(FailedReason failedReason){
        return switch (failedReason){
            case MISSING_ACCOUNT -> ResponseEntity.notFound().build();
            case UNKNOWN -> ResponseEntity.badRequest().build();
            case UNSUPPORTED_CURRENCY -> ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiError("Cannot convert currency from EUR to PLN."));
        };
    }
}
