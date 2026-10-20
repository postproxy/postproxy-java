package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LinkedInFormat {
    POST("post"),
    POLL("poll");

    private final String value;

    LinkedInFormat(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
