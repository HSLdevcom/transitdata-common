package fi.hsl.common.passengercount.json;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class ApcJson {
    @JsonAttribute(nullable = false, name = "APC")
    public Apc apc;
}
