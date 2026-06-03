package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record Comment(
        @JsonProperty("id") String id,
        @JsonProperty("external_id") String externalId,
        @JsonProperty("body") String body,
        @JsonProperty("status") String status,
        @JsonProperty("author_username") String authorUsername,
        @JsonProperty("author_avatar_url") String authorAvatarUrl,
        @JsonProperty("author_external_id") String authorExternalId,
        @JsonProperty("metadata") Map<String, Object> metadata,
        @JsonProperty("parent_external_id") String parentExternalId,
        @JsonProperty("like_count") int likeCount,
        @JsonProperty("is_hidden") boolean isHidden,
        @JsonProperty("permalink") String permalink,
        @JsonProperty("platform_data") Object platformData,
        @JsonProperty("attachments") List<Attachment> attachments,
        @JsonProperty("posted_at") String postedAt,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("replies") List<Comment> replies
) {}
