package fi.hsl.common.hfp;

import fi.hsl.common.hfp.proto.Hfp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public class HfpValidator {

    private static final Logger log = LoggerFactory.getLogger(HfpParser.class);

    public static Optional<String> validateString(String str) {
        if (str == null || str.isEmpty())
            return Optional.empty();
        else
            return Optional.of(str);
    }

    public static Optional<String> validateLocationQualityMethod(String str) {
        if (validateString(str).isPresent()) {
            switch (str) {
                case "GPS":
                case "ODO":
                case "MAND":
                case "N/A":
                    return Optional.of(str);
                default:
                    log.warn("Received unknown location quality method: {}", str);
                    return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static Optional<Hfp.Payload.TlpRequestType> validateTlpRequestType(String str) {
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

    public static Optional<Hfp.Payload.TlpPriorityLevel> validateTlpPriorityLevel(String str) {
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

    public static Optional<Hfp.Payload.TlpReason> validateTlpReason(String str) {
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

    public static Optional<Hfp.Payload.TlpDecision> validateTlpDecision(String str) {
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
