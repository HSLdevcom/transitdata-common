package fi.hsl.eke;

import com.google.protobuf.*;


public class EkeParser {

    private EkeParser() {

    }

    public static EkeParser newInstance() {
        return new EkeParser();
    }

    public FiHslEke.EkeMessage parseEkeMessage(byte[] rawPayload) throws InvalidProtocolBufferException {
        return FiHslEke.EkeMessage.parseFrom(rawPayload);
    }

    public static class InvalidEkeMessageException extends RuntimeException {
    }
}
