package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WebhookDelivery(
        @JsonProperty("id") String id,
        @JsonProperty("event_id") String eventId,
        @JsonProperty("event_type") String eventType,
        @JsonProperty("response_status") Integer responseStatus,
        @JsonProperty("attempt_number") int attemptNumber,
        @JsonProperty("success") boolean success,
        @JsonProperty("attempted_at") String attemptedAt,
        @JsonProperty("created_at") String createdAt
) {}
