package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

/** What started a post sync run. */
public enum PostSyncTrigger {
    CONNECT("connect"),
    SCHEDULED("scheduled"),
    BACKFILL("backfill");

    private final String value;

    PostSyncTrigger(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
