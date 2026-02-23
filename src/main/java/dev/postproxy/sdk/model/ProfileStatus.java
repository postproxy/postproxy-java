package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProfileStatus {
    ACTIVE("active"),
    EXPIRED("expired"),
    INACTIVE("inactive");

    private final String value;

    ProfileStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
