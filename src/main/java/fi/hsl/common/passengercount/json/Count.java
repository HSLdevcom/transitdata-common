package fi.hsl.common.passengercount.json;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class Count{

    @JsonAttribute(nullable = false, name = "class")
    public String clazz;
    public int in;
    public int out;
}
