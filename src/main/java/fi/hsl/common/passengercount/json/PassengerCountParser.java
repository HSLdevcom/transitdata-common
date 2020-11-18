package fi.hsl.common.passengercount.json;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.ParsingException;
import com.dslplatform.json.runtime.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PassengerCountParser {
    private static final Logger log = LoggerFactory.getLogger(PassengerCountParser.class);

    final DslJson<Object> dslJson = new DslJson<>(Settings.withRuntime().allowArrayFormat(true).includeServiceLoader());

    @NotNull
    public static PassengerCountParser newInstance() {
        return new PassengerCountParser();
    }

    @Nullable
    public APCJson parseJson(@NotNull byte[] data) throws IOException, InvalidAPCPayloadException {
        try {
            return dslJson.deserialize(APCJson.class, data, data.length);
        } catch (IOException ioe) {
            if (ioe instanceof ParsingException) {
                throw new PassengerCountParser.InvalidAPCPayloadException("Failed to parse APC JSON", (ParsingException)ioe);
            } else {
                throw ioe;
            }
        }

    }

    public static class InvalidAPCTopicException extends Exception {
        private InvalidAPCTopicException(String message) {
            super(message);
        }
    }

    public static class InvalidAPCPayloadException extends Exception {
        private InvalidAPCPayloadException(String message, ParsingException cause) {
            super(message, cause);
        }
    }
}
