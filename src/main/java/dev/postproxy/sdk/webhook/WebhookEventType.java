package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WebhookEventType {
    POST_PROCESSED("post.processed"),
    POST_IMPORTED("post.imported"),
    PLATFORM_POST_PUBLISHED("platform_post.published"),
    PLATFORM_POST_FAILED("platform_post.failed"),
    PLATFORM_POST_FAILED_WAITING_FOR_RETRY("platform_post.failed_waiting_for_retry"),
    PLATFORM_POST_INSIGHTS("platform_post.insights"),
    PROFILE_CONNECTED("profile.connected"),
    PROFILE_DISCONNECTED("profile.disconnected"),
    PROFILE_STATS("profile.stats"),
    MEDIA_FAILED("media.failed"),
    COMMENT_CREATED("comment.created");

    private final String value;

    WebhookEventType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static WebhookEventType fromValue(String value) {
        for (WebhookEventType t : values()) {
            if (t.value.equals(value)) return t;
        }
        return null;
    }
}
