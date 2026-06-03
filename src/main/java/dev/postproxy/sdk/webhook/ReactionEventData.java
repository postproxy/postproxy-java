package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.Message;

public record ReactionEventData(
        @JsonProperty("message") Message message,
        @JsonProperty("sender_external_id") String senderExternalId,
        @JsonProperty("action") String action,
        @JsonProperty("reaction") String reaction,
        @JsonProperty("emoji") String emoji,
        @JsonProperty("occurred_at") String occurredAt
) {}
