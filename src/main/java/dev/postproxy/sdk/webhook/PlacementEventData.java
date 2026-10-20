package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.Platform;

import java.util.Map;

/**
 * Payload of the {@code placement.*} events. {@code id} is the placement's id on the platform
 * (page id, phone number id, ...). {@code placement.name_status_updated} adds {@code decision},
 * {@code requestedName} and {@code rejectionReason}; {@code placement.quality_updated} adds
 * {@code event}, {@code currentLimit} and {@code oldLimit}. They are null otherwise.
 */
public record PlacementEventData(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("platform") Platform platform,
        @JsonProperty("profile_group_id") String profileGroupId,
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("profile_name") String profileName,
        @JsonProperty("metadata") Map<String, Object> metadata,
        @JsonProperty("decision") String decision,
        @JsonProperty("requested_name") String requestedName,
        @JsonProperty("rejection_reason") String rejectionReason,
        @JsonProperty("event") String event,
        @JsonProperty("current_limit") String currentLimit,
        @JsonProperty("old_limit") String oldLimit
) {}
