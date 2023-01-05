package fi.hsl.common.hfp;

import fi.hsl.common.hfp.proto.Hfp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Null;
import java.util.Optional;


public class HfpValidator {

    private static final Logger log = LoggerFactory.getLogger(HfpParser.class);

    public static Optional<String> validateString(@Nullable String str) {
        if (str == null || str.isEmpty())
            return Optional.empty();
        else
            return Optional.of(str);
    }

    public static Optional<String> validateLocationQualityMethod(@Nullable String str) {
        if (validateString(str).isPresent()) {
            switch (str) {
                case "GPS":
                case "ODO":
                case "MAN":
                case "DR":
                case "N/A":
                    return Optional.of(str);
                default:
                    log.warn("Received unknown location quality method: {}", str);
                    return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static Optional<Hfp.Payload.TlpRequestType> validateTlpRequestType(@Nullable String str) {
        if (validateString(str).isPresent()) {
            try {
                return Optional.of(Hfp.Payload.TlpRequestType.valueOf(str));
            } catch (Exception e) {
                log.warn("Received unknown tlp request type: {}", str);
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static Optional<Hfp.Payload.TlpPriorityLevel> validateTlpPriorityLevel(@Nullable String str) {
        if (validateString(str).isPresent()) {
            try {
                return Optional.of(Hfp.Payload.TlpPriorityLevel.valueOf(str));
            } catch (Exception e) {
                log.warn("Received unknown tlp priority level: {}", str);
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static Optional<Hfp.Payload.TlpReason> validateTlpReason(@Nullable String str) {
        if (validateString(str).isPresent()) {
            try {
                return Optional.of(Hfp.Payload.TlpReason.valueOf(str));
            } catch (Exception e) {
                log.warn("Received unknown tlp reason: {}", str);
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static Optional<Hfp.Payload.TlpDecision> validateTlpDecision(@Nullable String str) {
        if (validateString(str).isPresent()) {
            try {
                return Optional.of(Hfp.Payload.TlpDecision.valueOf(str));
            } catch (Exception e) {
                log.warn("Received unknown tlp decision: {}", str);
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
