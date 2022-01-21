package fi.hsl.common.passengercount;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.ParsingException;
import com.dslplatform.json.runtime.Settings;
import fi.hsl.common.hfp.HfpJson;
import fi.hsl.common.hfp.HfpValidator;
import fi.hsl.common.hfp.proto.Hfp;
import fi.hsl.common.passengercount.json.*;
import fi.hsl.common.passengercount.proto.PassengerCount;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

public class PassengerCountParser {
    private static final Logger log = LoggerFactory.getLogger(PassengerCountParser.class);

    final DslJson<Object> dslJson = new DslJson<>(Settings.withRuntime().allowArrayFormat(true).includeServiceLoader());

    @NotNull
    public static PassengerCountParser newInstance() {
        return new PassengerCountParser();
    }

    @NotNull
    public PassengerCount.Payload parsePayload(@NotNull APCJson json) {
        final APC payload = json.apc;

        // Required attributes
        PassengerCount.Payload.Builder payloadBuilder = PassengerCount.Payload.newBuilder();
        payloadBuilder.setDesi(payload.desi);
        payloadBuilder.setDir(payload.dir);
        payloadBuilder.setOper(payload.oper);
        payloadBuilder.setVeh(payload.veh);
        payloadBuilder.setTst(payload.tst.getTime());
        payloadBuilder.setTsi(payload.tsi);
        payloadBuilder.setLat(payload.lat);
        payloadBuilder.setLong(payload.lon);
        payloadBuilder.setOdo(payload.odo);
        payloadBuilder.setOday(payload.oday);
        payloadBuilder.setJrn(payload.jrn);
        payloadBuilder.setLine(payload.line);
        payloadBuilder.setStart(payload.start);
        payloadBuilder.setLoc(payload.loc);
        if (payload.stop != null) {
            payloadBuilder.setStop(payload.stop);
        }
        payloadBuilder.setRoute(payload.route);

        PassengerCount.VehicleCounts.Builder vehicleBuilder = PassengerCount.VehicleCounts.newBuilder();
        vehicleBuilder.setVehicleLoad(payload.vehiclecounts.vehicleload);
        vehicleBuilder.setVehicleLoadRatio(payload.vehiclecounts.vehicleloadratio);
        vehicleBuilder.setCountQuality(payload.vehiclecounts.countquality);
        if (payload.vehiclecounts.extensions != null) {
            vehicleBuilder.setExtensions(payload.vehiclecounts.extensions);
        }

        for (DoorCount doorcount : payload.vehiclecounts.doorcounts) {
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
        return payloadBuilder.build();
    }

    public APCJson toJson(PassengerCount.Payload passengerCountPayload){
        APCJson apcJson = new APCJson();
        apcJson.apc = new APC();
        apcJson.apc.veh = passengerCountPayload.getVeh();
        apcJson.apc.desi = passengerCountPayload.getDesi();
        apcJson.apc.loc = passengerCountPayload.getLoc();
        apcJson.apc.dir = passengerCountPayload.getDir();
        apcJson.apc.oday = passengerCountPayload.getOday();
        apcJson.apc.oper = passengerCountPayload.getOper();
        apcJson.apc.route = passengerCountPayload.getRoute();
        apcJson.apc.start = passengerCountPayload.getStart();
        apcJson.apc.stop = passengerCountPayload.getStop();
        apcJson.apc.tst = new Date(passengerCountPayload.getTst());
        apcJson.apc.jrn = passengerCountPayload.getJrn();
        apcJson.apc.lat = passengerCountPayload.getLat();
        apcJson.apc.line = passengerCountPayload.getLine();
        apcJson.apc.lon = passengerCountPayload.getLong();
        apcJson.apc.odo = passengerCountPayload.getOdo();
        apcJson.apc.tsi = passengerCountPayload.getTsi();

        apcJson.apc.vehiclecounts = new Vehiclecounts();
        apcJson.apc.vehiclecounts.countquality = passengerCountPayload.getVehicleCounts().getCountQuality();
        apcJson.apc.vehiclecounts.vehicleload = passengerCountPayload.getVehicleCounts().getVehicleLoad();
        apcJson.apc.vehiclecounts.extensions = passengerCountPayload.getVehicleCounts().getExtensions();
        apcJson.apc.vehiclecounts.vehicleloadratio = passengerCountPayload.getVehicleCounts().getVehicleLoadRatio();
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
