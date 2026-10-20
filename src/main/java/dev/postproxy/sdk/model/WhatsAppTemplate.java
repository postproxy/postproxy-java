package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A WhatsApp message template. {@code status} follows Meta's review (e.g. PENDING, APPROVED, REJECTED). */
public record WhatsAppTemplate(
        @JsonProperty("id") String id,
        @JsonProperty("external_id") String externalId,
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("name") String name,
        @JsonProperty("language") String language,
        @JsonProperty("category") String category,
        @JsonProperty("status") String status,
        /** {@code POSITIONAL} or {@code NAMED}. */
        @JsonProperty("parameter_format") String parameterFormat,
        @JsonProperty("components") List<Map<String, Object>> components,
        @JsonProperty("message_send_ttl_seconds") Integer messageSendTtlSeconds,
        @JsonProperty("rejected_reason") String rejectedReason,
        @JsonProperty("quality_score") String qualityScore,
        @JsonProperty("variable_count") int variableCount,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("updated_at") String updatedAt
) {}
