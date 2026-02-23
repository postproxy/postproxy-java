package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum YouTubeFormat {
    POST("post");

    private final String value;

    YouTubeFormat(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
