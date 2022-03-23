package pl.cleankod.exchange.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Converter service",
                version = "0.0.1",
                description = "This is an example project that calculates the amount balance to a given currency."
        )
)
public class OpenApiConfiguration {
}
