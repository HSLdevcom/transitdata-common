package fi.hsl.common.passengercount.json;

import com.dslplatform.json.CompiledJson;

import java.util.List;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class Vehiclecounts {
    public String countquality;
    public int vehicleload;
    public String vehicleloadratio;
    public List<DoorCount> doorcounts;
    public String extensions;
}