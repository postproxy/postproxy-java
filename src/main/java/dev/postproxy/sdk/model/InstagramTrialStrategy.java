package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

/** How an Instagram trial reel graduates to your followers. */
public enum InstagramTrialStrategy {
    MANUAL("MANUAL"),
    SS_PERFORMANCE("SS_PERFORMANCE");

    private final String value;

    InstagramTrialStrategy(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
