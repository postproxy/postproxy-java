package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload of {@code chat.thread_control_changed}: another Meta app passed or took a Facebook /
 * Instagram conversation. {@code previousOwnerAppId} is null when the conversation was idle.
 */
public record ThreadControlChangedEventData(
        @JsonProperty("chat") WebhookChatData chat,
        @JsonProperty("previous_owner_app_id") String previousOwnerAppId,
        @JsonProperty("new_owner_app_id") String newOwnerAppId,
        @JsonProperty("metadata") String metadata,
        @JsonProperty("occurred_at") String occurredAt
) {}
