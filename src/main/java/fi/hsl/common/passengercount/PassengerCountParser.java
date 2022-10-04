package fi.hsl.common.passengercount;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.ParsingException;
import com.dslplatform.json.runtime.Settings;
import fi.hsl.common.passengercount.json.*;
import fi.hsl.common.passengercount.proto.PassengerCount;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class PassengerCountParser {
    private static final Logger log = LoggerFactory.getLogger(PassengerCountParser.class);

    final DslJson<Object> dslJson = new DslJson<>(Settings.withRuntime().allowArrayFormat(true).includeServiceLoader());

    @NotNull
    public static PassengerCountParser newInstance() {
        return new PassengerCountParser();
    }

    @NotNull
    public Optional<PassengerCount.Payload> parsePayload(@NotNull APCJson json) {
        final APC payload = json.apc;

        // Required attributes
        PassengerCount.Payload.Builder payloadBuilder = PassengerCount.Payload.newBuilder();
        payloadBuilder.setDesi(payload.desi);
        payloadBuilder.setDir(payload.dir);

        final OptionalInt maybeOper = safeParseInt(payload.oper);
        if (!maybeOper.isPresent()) {
            log.warn("Failed to parse oper from {}", payload.oper);
            //oper value is mandatory -> return empty
            return Optional.empty();
        }
        maybeOper.ifPresent(payloadBuilder::setOper);

        final OptionalInt maybeVeh = safeParseInt(payload.veh);
        if (!maybeVeh.isPresent()) {
            log.warn("Failed to parse veh from {}", payload.veh);
            //veh value is mandatory -> return empty
            return Optional.empty();
        }
        maybeVeh.ifPresent(payloadBuilder::setVeh);

        payloadBuilder.setTst(payload.tst.getTime());
        payloadBuilder.setTsi(payload.tsi);

        final OptionalDouble maybeLat = safeParseDouble(payload.lat);
        if (!maybeLat.isPresent()) {
            log.warn("Failed to parse lat from {}", payload.lat);
        }
        maybeLat.ifPresent(payloadBuilder::setLat);

        final OptionalDouble maybeLon = safeParseDouble(payload.lon);
        if (!maybeLon.isPresent()) {
            log.warn("Failed to parse lon from {}", payload.lon);
        }
        maybeLon.ifPresent(payloadBuilder::setLong);

        final OptionalDouble maybeOdo = safeParseDouble(payload.odo);
        if (!maybeOdo.isPresent()) {
            log.warn("Failed to parse odo from {}", payload.odo);
        }
        maybeOdo.ifPresent(payloadBuilder::setOdo);

        payloadBuilder.setOday(payload.oday);

        final OptionalInt maybeJrn = safeParseInt(payload.jrn);
        if (!maybeJrn.isPresent()) {
            log.warn("Failed to parse jrn from {}", payload.jrn);
        }
        maybeJrn.ifPresent(payloadBuilder::setJrn);

        final OptionalInt maybeLine = safeParseInt(payload.line);
        if (!maybeLine.isPresent()) {
            log.warn("Failed to parse line from {}", payload.line);
        }
        maybeLine.ifPresent(payloadBuilder::setLine);

        payloadBuilder.setStart(payload.start);
        payloadBuilder.setLoc(payload.loc);
        safeParseInt(payload.stop).ifPresent(payloadBuilder::setStop);
        payloadBuilder.setRoute(payload.route);

        if (payload.vehiclecounts == null) {
            log.warn("Field 'vehiclecounts' was null for vehicle {}/{}", payload.oper, payload.veh);
            return Optional.empty();
        }

        PassengerCount.VehicleCounts.Builder vehicleBuilder = PassengerCount.VehicleCounts.newBuilder();
        vehicleBuilder.setVehicleLoad(payload.vehiclecounts.vehicleload);

        final OptionalDouble maybeVehicleLoadRatio = safeParseDouble(payload.vehiclecounts.vehicleloadratio);
        if (!maybeVehicleLoadRatio.isPresent()) {
            log.warn("Failed to parse vehicleloadratio from {}", payload.vehiclecounts.vehicleloadratio);
        }
        maybeVehicleLoadRatio.ifPresent(vehicleBuilder::setVehicleLoadRatio);

        vehicleBuilder.setCountQuality(payload.vehiclecounts.countquality);
        if (payload.vehiclecounts.extensions != null) {
            vehicleBuilder.setExtensions(payload.vehiclecounts.extensions);
        }

        Collection<DoorCount> doorcounts = payload.vehiclecounts.doorcounts != null ? payload.vehiclecounts.doorcounts : Collections.emptyList();
        for (DoorCount doorcount : doorcounts) {
            PassengerCount.DoorCount.Builder doorCountBuilder = PassengerCount.DoorCount.newBuilder();
            doorCountBuilder.setDoor(doorcount.door);

            for (Count count : doorcount.count) {
                PassengerCount.Count.Builder countBuilder = PassengerCount.Count.newBuilder();
                countBuilder.setIn(count.in);
                countBuilder.setOut(count.out);
                countBuilder.setClazz(count.clazz);
                doorCountBuilder.addCount(countBuilder);
            }
            vehicleBuilder.addDoorCounts(doorCountBuilder);
        }
        payloadBuilder.setVehicleCounts(vehicleBuilder);
        return Optional.of(payloadBuilder.build());
    }

    public APCJson toJson(PassengerCount.Payload passengerCountPayload){
        APCJson apcJson = new APCJson();
        apcJson.apc = new APC();
        apcJson.apc.veh = String.valueOf(passengerCountPayload.getVeh());
        apcJson.apc.desi = passengerCountPayload.getDesi();
        apcJson.apc.loc = passengerCountPayload.getLoc();
        apcJson.apc.dir = passengerCountPayload.getDir();
        apcJson.apc.oday = passengerCountPayload.getOday();
        apcJson.apc.oper = String.valueOf(passengerCountPayload.getOper());
        apcJson.apc.route = passengerCountPayload.getRoute();
        apcJson.apc.start = passengerCountPayload.getStart();
        apcJson.apc.stop = String.valueOf(passengerCountPayload.getStop());
        apcJson.apc.tst = new Date(passengerCountPayload.getTst());
        apcJson.apc.jrn = String.valueOf(passengerCountPayload.getJrn());
        apcJson.apc.lat = String.valueOf(passengerCountPayload.getLat());
        apcJson.apc.line = String.valueOf(passengerCountPayload.getLine());
        apcJson.apc.lon = String.valueOf(passengerCountPayload.getLong());
        apcJson.apc.odo = String.valueOf(passengerCountPayload.getOdo());
        apcJson.apc.tsi = passengerCountPayload.getTsi();

        apcJson.apc.vehiclecounts = new Vehiclecounts();
        apcJson.apc.vehiclecounts.countquality = passengerCountPayload.getVehicleCounts().getCountQuality();
        apcJson.apc.vehiclecounts.vehicleload = passengerCountPayload.getVehicleCounts().getVehicleLoad();
        apcJson.apc.vehiclecounts.extensions = passengerCountPayload.getVehicleCounts().getExtensions();
        apcJson.apc.vehiclecounts.vehicleloadratio = String.valueOf(passengerCountPayload.getVehicleCounts().getVehicleLoadRatio());
        apcJson.apc.vehiclecounts.doorcounts = new ArrayList<>();
        for (PassengerCount.DoorCount doorCount : passengerCountPayload.getVehicleCounts().getDoorCountsList()) {
            DoorCount dc = new DoorCount();
            dc.count = new ArrayList<>();
            for (PassengerCount.Count count : doorCount.getCountList()) {
                Count c = new Count();
                c.clazz = count.getClazz();
                c.in = count.getIn();
                c.out = count.getOut();
                dc.count.add(c);
            }
            dc.door = doorCount.getDoor();
            apcJson.apc.vehiclecounts.doorcounts.add(dc);
        }
        return apcJson;
    }


    public OutputStream serializeJson(APCJson apcJson, OutputStream outputStream) throws IOException {
        dslJson.serialize(apcJson, outputStream);
        return outputStream;
    }


    @Nullable
    public APCJson parseJson(byte @NotNull [] data) throws IOException, InvalidAPCPayloadException {
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

    private static OptionalDouble safeParseDouble(String s) {
        try {
            return OptionalDouble.of(Double.parseDouble(s));
        } catch (NumberFormatException nfe) {
            return OptionalDouble.empty();
        }
    }

    private static OptionalInt safeParseInt(String s) {
        try {
            return OptionalInt.of(Integer.parseInt(s));
        } catch (NumberFormatException nfe) {
            return OptionalInt.empty();
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
