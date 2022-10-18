package com.georgeracu.blog.code.examples;

import java.util.Optional;
import java.util.Random;

/**
 * Author georgicaracu
 */
public final class OptionalExample {

    private OptionalExample() {
        // nothing to see here
    }

    private static final Random random = new Random();

    public static String mayNotReturnAString() {
        return random.nextInt() % 2 == 0 ? "Blah" : null;
    }

    public static Optional<String> maybeString() {
        return random.nextInt() % 2 == 1 ? Optional.of("Boo") : Optional.empty();
    }

    public static void handleStrings() {
        if (null != mayNotReturnAString()) {
            System.out.printf("No Optional value is not null: %s%n", mayNotReturnAString());
        } else {
            System.out.println("No Optional value is null");
        }

        maybeString().ifPresentOrElse(
                s -> System.out.printf("Optional value is not null: %s%n", s),
                () -> System.out.println("Optional value is null"));
    }

    public static Optional<String> notOverloaded(String mandatoryArg, Optional<String> maybeArg) {
        String accumulator = null;
        if (maybeArg.isPresent()) {
            accumulator = maybeArg.get();
        }
        if (null != mandatoryArg) {
            if (null != accumulator) {
                accumulator = accumulator.concat(mandatoryArg);
            } else {
                accumulator = mandatoryArg;
            }
        }

        return Optional.ofNullable(accumulator);
    }

    public static Optional<String> overloaded(String mandatoryArg) {
        return Optional.ofNullable(mandatoryArg);
    }

    public static Optional<String> overloaded(String mandatoryArg, String secondArg) {
        if (null != mandatoryArg) {
            if (null != secondArg) {
                return overloaded(secondArg.concat(mandatoryArg));
            } else {
                return overloaded(mandatoryArg);
            }

        } else {
            return overloaded(secondArg);
        }
    }
}
