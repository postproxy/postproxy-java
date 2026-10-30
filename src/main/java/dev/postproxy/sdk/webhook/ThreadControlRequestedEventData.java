package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Payload of {@code chat.thread_control_requested}: another Meta app asked PostProxy to pass a conversation. */
public record ThreadControlRequestedEventData(
        @JsonProperty("chat") WebhookChatData chat,
        @JsonProperty("requested_owner_app_id") String requestedOwnerAppId,
        @JsonProperty("metadata") String metadata,
        @JsonProperty("occurred_at") String occurredAt
) {}
