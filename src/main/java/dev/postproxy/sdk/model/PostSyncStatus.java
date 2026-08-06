package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

/** The state of a post sync run. */
public enum PostSyncStatus {
    PENDING("pending"),
    RUNNING("running"),
    COMPLETED("completed"),
    FAILED("failed");

    private final String value;

    PostSyncStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
