package pl.cleankod;

import io.micronaut.runtime.Micronaut;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApplicationInitializer {
    public static void main(String[] args) {
        Micronaut.run(ApplicationInitializer.class, args);
    }
}
