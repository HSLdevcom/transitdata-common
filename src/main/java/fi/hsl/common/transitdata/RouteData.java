package fi.hsl.common.transitdata;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//TODO refactor this class into the protobuf schema
public class RouteData {
    private int direction;
    private String routeName;
    private String operatingDay;
    private String startTime;
    private long stopId;

    public static final SimpleDateFormat START_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    public RouteData() {}

    public RouteData(long stopId, int direction, String routeName, long startDateTimeEpochSecs) {
        this(stopId, direction, routeName, null, null);
        setStartDateTime(startDateTimeEpochSecs);
    }

    public RouteData(long stopId, int direction, String routeName, String operatingDay, String startTime) {
        this.stopId = stopId;
        this.direction = direction;
        this.routeName = routeName;
        this.operatingDay = operatingDay;
        this.startTime = startTime;
    }

    public void setStartDateTime(long epochSecs) {
        String startTimeAsString = START_TIME_FORMAT.format(new Date(epochSecs * 1000));
        String[] dateAndTime = startTimeAsString.split(" ");
        operatingDay = dateAndTime[0];
        startTime = dateAndTime[1];
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getOperatingDay() {
        return operatingDay;
    }

    public void setOperatingDay(String operatingDay) {
        this.operatingDay = operatingDay;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public long getStopId() {
        return stopId;
    }

    public void setStopId(long stopId) {
        this.stopId = stopId;
    }

    public Map<String, String> toMap() {
        Map<String, String> props = new HashMap<>();
        props.put(TransitdataProperties.KEY_DIRECTION, Integer.toString(direction));
        props.put(TransitdataProperties.KEY_ROUTE_NAME, routeName);
        props.put(TransitdataProperties.KEY_OPERATING_DAY, operatingDay);
        props.put(TransitdataProperties.KEY_START_TIME, startTime);
        props.put(TransitdataProperties.KEY_STOP_ID, Long.toString(stopId));
        return props;
    }
}
