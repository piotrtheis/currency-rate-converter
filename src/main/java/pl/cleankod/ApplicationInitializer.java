package pl.cleankod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import pl.cleankod.exchange.config.ExchangeConfiguration;
import pl.cleankod.exchange.config.OpenApiConfiguration;

@SpringBootConfiguration
@EnableAutoConfiguration
@Import({
        ExchangeConfiguration.class,
        OpenApiConfiguration.class
})
public class ApplicationInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationInitializer.class, args);
    }
}
