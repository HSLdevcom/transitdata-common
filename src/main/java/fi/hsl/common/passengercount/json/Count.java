package fi.hsl.common.passengercount.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Count {

    @JsonProperty(value = "class", required = true)
    public String clazz;
    public int in;
    public int out;
}
