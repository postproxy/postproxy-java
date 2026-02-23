package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ThreadsFormat {
    POST("post");

    private final String value;

    ThreadsFormat(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
