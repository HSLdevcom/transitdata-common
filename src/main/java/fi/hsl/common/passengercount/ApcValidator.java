package fi.hsl.common.passengercount;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ApcValidator {

    private static final Logger log = LoggerFactory.getLogger(PassengerCountParser.class);

    public static Optional<String> validateString(@Nullable String str) {
        if (str == null || str.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(str);
        }
    }
}
