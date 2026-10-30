package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WebhookEventType {
    POST_PROCESSED("post.processed"),
    POST_IMPORTED("post.imported"),
    PLATFORM_POST_PUBLISHED("platform_post.published"),
    PLATFORM_POST_FAILED("platform_post.failed"),
    PLATFORM_POST_FAILED_WAITING_FOR_RETRY("platform_post.failed_waiting_for_retry"),
    PLATFORM_POST_EDITED("platform_post.edited"),
    PLATFORM_POST_EDIT_FAILED("platform_post.edit_failed"),
    PLATFORM_POST_INSIGHTS("platform_post.insights"),
    PROFILE_CONNECTED("profile.connected"),
    PROFILE_DISCONNECTED("profile.disconnected"),
    PROFILE_STATS("profile.stats"),
    PROFILE_PLACEMENTS_SYNCED("profile.placements_synced"),
    PLACEMENT_ORPHANED("placement.orphaned"),
    PLACEMENT_RESTORED("placement.restored"),
    MEDIA_FAILED("media.failed"),
    COMMENT_CREATED("comment.created"),
    COMMENT_FAILED("comment.failed"),
    COMMENT_FAILED_WAITING_FOR_RETRY("comment.failed_waiting_for_retry"),
    COMMENT_DELIVERY_UNCONFIRMED("comment.delivery_unconfirmed"),
    COMMENT_EDITED("comment.edited"),
    COMMENT_EDIT_FAILED("comment.edit_failed"),
    PROFILE_COMMENT_CREATED("profile_comment.created"),
    PROFILE_COMMENT_FAILED("profile_comment.failed"),
    PROFILE_COMMENT_FAILED_WAITING_FOR_RETRY("profile_comment.failed_waiting_for_retry"),
    PROFILE_COMMENT_EDITED("profile_comment.edited"),
    PROFILE_COMMENT_DELETED("profile_comment.deleted"),
    MESSAGE_RECEIVED("message.received"),
    MESSAGE_SENT("message.sent"),
    MESSAGE_DELIVERED("message.delivered"),
    MESSAGE_READ("message.read"),
    MESSAGE_EDITED("message.edited"),
    MESSAGE_DELETED("message.deleted"),
    MESSAGE_FAILED_WAITING_FOR_RETRY("message.failed_waiting_for_retry"),
    MESSAGE_FAILED("message.failed"),
    REACTION_RECEIVED("reaction.received"),
    REFERRAL_RECEIVED("referral.received"),
    CHAT_THREAD_CONTROL_CHANGED("chat.thread_control_changed"),
    CHAT_THREAD_CONTROL_REQUESTED("chat.thread_control_requested"),
    MESSAGE_TEMPLATE_STATUS_UPDATED("message_template.status_updated"),
    MESSAGE_TEMPLATE_CATEGORY_UPDATED("message_template.category_updated"),
    PLACEMENT_NAME_STATUS_UPDATED("placement.name_status_updated"),
    PLACEMENT_QUALITY_UPDATED("placement.quality_updated"),
    WHATSAPP_AUTOMATIC_EVENT("whatsapp.automatic_event");

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
