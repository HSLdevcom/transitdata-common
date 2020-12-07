package fi.hsl.common.passengercount.json;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

import java.util.Date;


@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)



public class APC {
    public String desi;
    public String dir;
    public int oper;
    public int veh;
    public Date tst;
    public long tsi;
    public double lat;
    @JsonAttribute(name = "long")
    public double lon;
    public double odo;
    public String oday;
    public int jrn;
    public int line;
    public String start;
    public String loc;
    public int stop;
    public String route;
    public Vehiclecounts vehiclecounts;
}