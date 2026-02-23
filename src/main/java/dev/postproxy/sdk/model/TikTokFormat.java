package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TikTokFormat {
    VIDEO("video"),
    IMAGE("image");

    private final String value;

    TikTokFormat(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
