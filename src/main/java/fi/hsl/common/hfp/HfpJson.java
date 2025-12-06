package fi.hsl.common.hfp;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HfpJson {
    //Specification: https://digitransit.fi/en/developers/apis/4-realtime-api/vehicle-positions/
    //Example payload:
    // {"VP":{"desi":"81","dir":"2","oper":22,"veh":792,"tst":"2018-04-05T17:38:36Z","tsi":1522949916,"spd":0.16,"hdg":225,"lat":60.194481,"long":25.03095,"acc":0,"dl":-25,"odo":2819,"drst":0,"oday":"2018-04-05","jrn":636,"line":112,"start":"20:25"}}

    @JsonProperty("VP")
    @JsonAlias({"DUE", "ARR", "DEP", "ARS", "PDE", "PAS", "WAIT",
            "DOO", "DOC", "TLR", "TLA", "DA", "DOUT", "BA", "BOUT",
            "VJA", "VJOUT"})
    public Payload payload;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload {

        public String desi;

        public String dir;

        public Integer oper;

        public Integer veh;

        @JsonProperty(required = true)
        public String tst;

        @JsonProperty(required = true)
        public long tsi;

        public Double spd;

        public Integer hdg;

        public Double lat;

        @JsonProperty("long") //use alternative name in JSON
        public Double longitude;

        public Double acc;

        public Integer dl;

        @JsonDeserialize(using = OdoDeserializer.class)
        @JsonSerialize(using = OdoSerializer.class)
        public Double odo;

        public Integer drst;

        public String oday;

        public Integer jrn;

        public Integer line;

        public String start; //%H:%M in 24 hour clock

        public String loc; // v2

        public Integer stop; // v2

        public String route; // v2

        public Integer occu; // v2

        public Integer seq;

        public String ttarr;

        public String ttdep;

        @JsonProperty("dr-type") //use alternative name in JSON
        public Integer dr_type;

        @JsonProperty("tlp-requestid")
        public Integer tlp_requestid;

        @JsonProperty("tlp-requesttype")
        public String tlp_requesttype;

        @JsonProperty("tlp-prioritylevel")
        public String tlp_prioritylevel;

        @JsonProperty("tlp-reason")
        public String tlp_reason;

        @JsonProperty("tlp-att-seq")
        public Integer tlp_att_seq;

        @JsonProperty("tlp-decision")
        public String tlp_decision;

        public Integer sid;

        @JsonProperty("signal-groupid")
        public Integer signal_groupid;

        @JsonProperty("tlp-signalgroupnbr")
        public Integer tlp_signalgroupnbr;

        @JsonProperty("tlp-line-configid")
        public Integer tlp_line_configid;

        @JsonProperty("tlp-point-configid")
        public Integer tlp_point_configid;

        @JsonProperty("tlp-frequency")
        public Integer tlp_frequency;

        @JsonProperty("tlp-protocol")
        public String tlp_protocol;

        public String label;
    }

    public static class OdoDeserializer extends JsonDeserializer<Double> {
        @Override
        public Double deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            if (p.currentToken().isNumeric()) {
                return p.getDoubleValue();
            }
            return null;
        }
    }

    public static class OdoSerializer extends JsonSerializer<Double> {
        @Override
        public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeNumber(value.intValue());
            }
        }
    }
}
