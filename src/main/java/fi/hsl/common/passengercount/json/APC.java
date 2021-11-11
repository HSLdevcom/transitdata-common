package fi.hsl.common.passengercount.json;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

import java.util.Date;


@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class APC {
    public String desi;
    public String dir;
    public Integer oper;
    public Integer veh;
    public Date tst;
    public Long tsi;
    public Double lat;
    @JsonAttribute(name = "long")
    public Double lon;
    public Double odo;
    public String oday;
    public Integer jrn;
    public Integer line;
    public String start;
    public String loc;
    public Integer stop;
    public String route;
    public Vehiclecounts vehiclecounts;
}