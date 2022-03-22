package pl.cleankod.exchange.entrypoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.entrypoint.model.AccountViewModel;
import pl.cleankod.exchange.entrypoint.model.ApiError;

@RestController
@RequestMapping("/accounts")
public interface AccountResource {

    @Operation(summary = "Get account by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account details", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AccountViewModel.class))}),
            @ApiResponse(responseCode = "400", description = "Validation error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "404", description = "Missing account", content = @Content),
    })
    @GetMapping(path = "/{id}")
    ResponseEntity<?> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency);

    @Operation(summary = "Get account by number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account details", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AccountViewModel.class))}),
            @ApiResponse(responseCode = "400", description = "Validation error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "404", description = "Missing account", content = @Content),
    })
    @GetMapping(path = "/number={number}")
    ResponseEntity<?> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency);
}
