package fi.hsl.common.passengercount.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DoorCount {
    public String door;
    public List<Count> count;
}