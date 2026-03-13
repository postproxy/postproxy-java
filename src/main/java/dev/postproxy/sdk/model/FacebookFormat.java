package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FacebookFormat {
    POST("post"),
    STORY("story"),
    REEL("reel");

    private final String value;

    FacebookFormat(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
