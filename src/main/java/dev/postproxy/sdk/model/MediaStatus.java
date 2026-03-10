package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MediaStatus {
    PENDING("pending"),
    PROCESSED("processed"),
    FAILED("failed");

    private final String value;

    MediaStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
