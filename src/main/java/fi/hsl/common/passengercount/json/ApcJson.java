package fi.hsl.common.passengercount.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApcJson {

    @JsonProperty(value = "APC", required = true)
    public Apc apc;
}
