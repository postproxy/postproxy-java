package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum YouTubePrivacy {
    PUBLIC("public"),
    UNLISTED("unlisted"),
    PRIVATE("private");

    private final String value;

    YouTubePrivacy(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
