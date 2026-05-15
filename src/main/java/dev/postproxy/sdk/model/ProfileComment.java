package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record ProfileComment(
        @JsonProperty("id") String id,
        @JsonProperty("external_id") String externalId,
        @JsonProperty("parent_external_id") String parentExternalId,
        @JsonProperty("placement_id") String placementId,
        @JsonProperty("body") String body,
        @JsonProperty("status") String status,
        @JsonProperty("author_username") String authorUsername,
        @JsonProperty("author_avatar_url") String authorAvatarUrl,
        @JsonProperty("platform_data") Map<String, Object> platformData,
        @JsonProperty("posted_at") String postedAt,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("replies") List<ProfileComment> replies
) {}
