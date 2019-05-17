package fi.hsl.common.hfp;

import com.dslplatform.json.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.time.OffsetDateTime;

// ignore unknown properties (default for objects).
// to disallow unknown properties in JSON set it to FAIL which will result in exception instead
@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class HfpJson {
    //Specification: https://digitransit.fi/en/developers/apis/4-realtime-api/vehicle-positions/
    //Example payload:
    // {"VP":{"desi":"81","dir":"2","oper":22,"veh":792,"tst":"2018-04-05T17:38:36Z","tsi":1522949916,"spd":0.16,"hdg":225,"lat":60.194481,"long":25.03095,"acc":0,"dl":-25,"odo":2819,"drst":0,"oday":"2018-04-05","jrn":636,"line":112,"start":"20:25"}}

    @JsonAttribute(nullable = false)
    public Payload VP;

    @CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
    public static class Payload {

        public String desi;

        public String dir;

        public Integer oper;

        public Integer veh;

        @JsonAttribute(nullable = false)
        public String tst;

        @JsonAttribute(nullable = false)
        public long tsi;

        public Double spd;

        public Integer hdg;

        public Double lat;

        @JsonAttribute(name = "long") //use alternative name in JSON
        public Double longitude;

        public Double acc;

        public Integer dl;

        @JsonAttribute(converter = Odo.class)
        public Double odo;

        public Integer drst;

        public String oday;

        public Integer jrn;

        public Integer line;

        public String start; //%H:%M in 24 hour clock

        @JsonAttribute(ignore = true)
        public String loc;

        @JsonAttribute(ignore = true)
        public Integer stop;

        @JsonAttribute(ignore = true)
        public String route;

        @JsonAttribute(ignore = true)
        public Integer occu;
    }

    public static abstract class Odo {
        public static final JsonReader.ReadObject<Double> JSON_READER = new JsonReader.ReadObject<Double>() {
            public Double read(JsonReader reader) throws IOException {
                return NumberConverter.deserializeDouble(reader);
            }
        };

        public static final JsonWriter.WriteObject<Double> JSON_WRITER = new JsonWriter.WriteObject<Double>() {
            public void write(JsonWriter writer, Double value) {
                if (value == null) {
                    writer.writeNull();
                } else {
                    NumberConverter.serializeNullable(value.intValue(), writer);
                }
            }
        };
    }

}
