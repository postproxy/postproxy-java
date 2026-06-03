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
    COMMENT_CREATED("comment.created"),
    PROFILE_COMMENT_CREATED("profile_comment.created"),
    MESSAGE_RECEIVED("message.received"),
    MESSAGE_SENT("message.sent"),
    MESSAGE_DELIVERED("message.delivered"),
    MESSAGE_READ("message.read"),
    MESSAGE_EDITED("message.edited"),
    MESSAGE_DELETED("message.deleted"),
    MESSAGE_FAILED_WAITING_FOR_RETRY("message.failed_waiting_for_retry"),
    MESSAGE_FAILED("message.failed"),
    REACTION_RECEIVED("reaction.received");

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
