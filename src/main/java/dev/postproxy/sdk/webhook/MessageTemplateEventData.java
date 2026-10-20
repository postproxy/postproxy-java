package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.Platform;

/** Payload of {@code message_template.status_updated} and {@code message_template.category_updated}. */
public record MessageTemplateEventData(
        @JsonProperty("id") String id,
        @JsonProperty("external_id") String externalId,
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("platform") Platform platform,
        @JsonProperty("name") String name,
        @JsonProperty("language") String language,
        @JsonProperty("category") String category,
        @JsonProperty("status") String status,
        @JsonProperty("rejected_reason") String rejectedReason,
        @JsonProperty("quality_score") String qualityScore
) {}
