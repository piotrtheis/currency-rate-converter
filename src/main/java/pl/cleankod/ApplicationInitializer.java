package pl.cleankod;

import io.micronaut.runtime.Micronaut;

public class ApplicationInitializer {
    public static void main(String[] args) {
        Micronaut.run(ApplicationInitializer.class, args);
    }
}
