package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Platform {
    FACEBOOK("facebook"),
    INSTAGRAM("instagram"),
    TIKTOK("tiktok"),
    LINKEDIN("linkedin"),
    YOUTUBE("youtube"),
    TWITTER("twitter"),
    THREADS("threads"),
    PINTEREST("pinterest");

    private final String value;

    Platform(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
