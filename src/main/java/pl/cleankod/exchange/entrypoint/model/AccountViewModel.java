package pl.cleankod.exchange.entrypoint.model;

import pl.cleankod.exchange.core.domain.Account;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountViewModel(String id, String number, BalanceViewModel balance) {

    public static record BalanceViewModel(BigDecimal amount, String currency){}

    public static AccountViewModel from(Account account) {
        var balance = new BalanceViewModel(account.balance().amount(), account.balance().currency().getCurrencyCode());
        return new AccountViewModel(account.id().value().toString(), account.number().value(), balance);
    }
}
