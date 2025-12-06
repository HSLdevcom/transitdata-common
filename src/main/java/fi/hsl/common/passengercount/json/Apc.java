package fi.hsl.common.passengercount.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties
public class Apc {
    public String desi;
    public String dir;
    public Integer oper;
    public Integer veh;
    public Date tst;
    public Long tsi;
    public Double lat;
    @JsonProperty("long")
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