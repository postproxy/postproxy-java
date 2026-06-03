package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MessageStatus {
    PENDING("pending"),
    PUBLISHED("published"),
    FAILED_WAITING_FOR_RETRY("failed_waiting_for_retry"),
    FAILED("failed"),
    RECEIVED("received");

    private final String value;

    MessageStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
