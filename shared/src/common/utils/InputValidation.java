package common.utils;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public final class InputValidation {
    private InputValidation() {}

    public static <T> void validateOrThrow(T value, Predicate<T> validator, Function<T, String> errorMessageProvider) {
        Optional.of(value)
                .filter(validator)
                .orElseThrow(() -> new IllegalArgumentException(errorMessageProvider.apply(value)));
    }

    public static boolean isInRange(int value, int min, int max) { return value >= min && value <= max; }
}
