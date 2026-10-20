package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.ErrorDetails;
import dev.postproxy.sdk.model.Platform;

import java.util.Map;

/**
 * Payload of the {@code profile_comment.*} events: {@code created}, {@code failed},
 * {@code failed_waiting_for_retry} and {@code deleted}.
 */
public record ProfileCommentCreatedData(
        @JsonProperty("id") String id,
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("platform") Platform platform,
        @JsonProperty("placement_id") String placementId,
        @JsonProperty("external_id") String externalId,
        @JsonProperty("parent_external_id") String parentExternalId,
        @JsonProperty("body") String body,
        @JsonProperty("status") String status,
        @JsonProperty("author_username") String authorUsername,
        @JsonProperty("author_avatar_url") String authorAvatarUrl,
        @JsonProperty("platform_data") Map<String, Object> platformData,
        @JsonProperty("error") String error,
        @JsonProperty("error_details") ErrorDetails errorDetails,
        @JsonProperty("permalink") String permalink,
        @JsonProperty("posted_at") String postedAt,
        @JsonProperty("created_at") String createdAt
) {}
