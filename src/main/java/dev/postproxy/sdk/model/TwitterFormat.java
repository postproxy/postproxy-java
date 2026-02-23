package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TwitterFormat {
    POST("post");

    private final String value;

    TwitterFormat(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
