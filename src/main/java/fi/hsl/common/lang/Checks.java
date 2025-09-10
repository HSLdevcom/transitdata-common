package fi.hsl.common.lang;

import java.util.Collection;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Checks {

    public static <T> T checkRequired(String paramName, T value) {
        if (value == null) {
            throw new IllegalArgumentException(paramName + " is required");
        }
        return value;
    }

    public static <T, C extends Collection<T>> C checkNotEmpty(String paramName, C collection) {
        if (collection == null) {
            throw new IllegalArgumentException(paramName + " must not be null");
        }
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(paramName + " must not be empty");
        }

        return collection;
    }

    public static String checkNotEmpty(String paramName, String value) {
        if (value == null) {
            throw new IllegalArgumentException(paramName + " must not be null");
        }
        if (isBlank(value)) {
            throw new IllegalArgumentException(paramName + " must not be blank");
        }

        return value;
    }

    public static void checkEither(boolean first, boolean second, String errorMessage) {
        if (first == second) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
