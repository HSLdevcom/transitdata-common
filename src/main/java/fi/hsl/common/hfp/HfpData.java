package fi.hsl.common.hfp;

public class HfpData {
    private final HfpTopic topic;
    private final HfpPayload payload;

    public HfpData(HfpTopic topic, HfpPayload payload) {
        this.topic = topic;
        this.payload = payload;
    }

    public HfpTopic getTopic() {
        return topic;
    }

    public HfpPayload getPayload() {
        return payload;
    }
}
