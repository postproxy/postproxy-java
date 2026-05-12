package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Webhook envelope. The {@code data} field is held as a JsonNode so callers can
 * decode it into the right typed payload via {@link WebhookEvents}.
 */
public record WebhookEvent(
        @JsonProperty("id") String id,
        @JsonProperty("type") WebhookEventType type,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("data") JsonNode data
) {}
