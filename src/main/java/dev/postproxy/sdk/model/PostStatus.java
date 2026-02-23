package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PostStatus {
    PENDING("pending"),
    DRAFT("draft"),
    PROCESSING("processing"),
    PROCESSED("processed"),
    SCHEDULED("scheduled");

    private final String value;

    PostStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
