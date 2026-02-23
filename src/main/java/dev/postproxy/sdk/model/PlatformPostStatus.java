package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PlatformPostStatus {
    PENDING("pending"),
    PROCESSING("processing"),
    PUBLISHED("published"),
    FAILED("failed"),
    DELETED("deleted");

    private final String value;

    PlatformPostStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
