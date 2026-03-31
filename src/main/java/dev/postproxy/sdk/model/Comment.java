package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record Comment(
        @JsonProperty("id") String id,
        @JsonProperty("external_id") String externalId,
        @JsonProperty("body") String body,
        @JsonProperty("status") String status,
        @JsonProperty("author_username") String authorUsername,
        @JsonProperty("author_avatar_url") String authorAvatarUrl,
        @JsonProperty("author_external_id") String authorExternalId,
        @JsonProperty("parent_external_id") String parentExternalId,
        @JsonProperty("like_count") int likeCount,
        @JsonProperty("is_hidden") boolean isHidden,
        @JsonProperty("permalink") String permalink,
        @JsonProperty("platform_data") Object platformData,
        @JsonProperty("posted_at") String postedAt,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("replies") List<Comment> replies
) {}
