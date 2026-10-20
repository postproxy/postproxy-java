package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

/** Why an inactive profile was deactivated; {@code NETWORK_TAKEN} means the same account was connected elsewhere. */
public enum ProfileInactiveReason {
    NETWORK_TAKEN("network_taken");

    private final String value;

    ProfileInactiveReason(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
