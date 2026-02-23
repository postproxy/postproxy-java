package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum InstagramFormat {
    POST("post"),
    REEL("reel"),
    STORY("story");

    private final String value;

    InstagramFormat(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
