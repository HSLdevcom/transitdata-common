package fi.hsl.common.passengercount.json;

import com.dslplatform.json.CompiledJson;

import java.util.List;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class DoorCount{
    public String door;
    public List<Count> count;
}