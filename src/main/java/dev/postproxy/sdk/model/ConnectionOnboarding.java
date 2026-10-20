package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Which connection flow to start. Instagram connects through {@code INSTAGRAM} (default) or
 * {@code FACEBOOK} (Facebook login — needed for reel music); WhatsApp through {@code API} (Cloud
 * API) or {@code BUSINESS_APP} (the WhatsApp Business app).
 */
public enum ConnectionOnboarding {
    INSTAGRAM("instagram"),
    FACEBOOK("facebook"),
    API("api"),
    BUSINESS_APP("business_app");

    private final String value;

    ConnectionOnboarding(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
