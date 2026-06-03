package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MessageDirection {
    INBOUND("inbound"),
    OUTBOUND("outbound");

    private final String value;

    MessageDirection(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
