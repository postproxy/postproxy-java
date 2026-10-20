package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * {@code PUBLISH} attributes post metrics to the day the post was published instead of the day
 * they were received.
 */
public enum DailyStatsAttribution {
    RECEIVED("received"),
    PUBLISH("publish");

    private final String value;

    DailyStatsAttribution(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
