package fi.hsl.common.passengercount.json;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

import java.util.Date;


@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class APC {
    public String desi;
    public String dir;
    public String oper;
    public String veh;
    public Date tst;
    public Long tsi;
    public String lat;
    @JsonAttribute(name = "long")
    public String lon;
    public String odo;
    public String oday;
    public String jrn;
    public String line;
    public String start;
    public String loc;
    public String stop;
    public String route;
    public Vehiclecounts vehiclecounts;
}