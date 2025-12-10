package fi.hsl.common.passengercount.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Vehiclecounts {
    public String countquality;
    public int vehicleload;
    public Double vehicleloadratio;
    public List<DoorCount> doorcounts;
    public String extensions;
}