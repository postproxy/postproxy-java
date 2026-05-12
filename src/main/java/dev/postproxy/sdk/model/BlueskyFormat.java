package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BlueskyFormat {
    POST("post");

    private final String value;

    BlueskyFormat(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
