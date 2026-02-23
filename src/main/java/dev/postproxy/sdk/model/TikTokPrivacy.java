package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TikTokPrivacy {
    PUBLIC_TO_EVERYONE("PUBLIC_TO_EVERYONE"),
    MUTUAL_FOLLOW_FRIENDS("MUTUAL_FOLLOW_FRIENDS"),
    FOLLOWER_OF_CREATOR("FOLLOWER_OF_CREATOR"),
    SELF_ONLY("SELF_ONLY");

    private final String value;

    TikTokPrivacy(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
