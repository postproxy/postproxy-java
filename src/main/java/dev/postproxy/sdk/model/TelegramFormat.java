package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TelegramFormat {
    POST("post");

    private final String value;

    TelegramFormat(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
