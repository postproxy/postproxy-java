package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.ErrorDetails;
import dev.postproxy.sdk.model.Platform;

import java.util.Map;

/**
 * Payload of the {@code comment.*} events: {@code created}, {@code failed},
 * {@code failed_waiting_for_retry}, {@code delivery_unconfirmed}, {@code edited} and
 * {@code edit_failed}.
 */
public record CommentCreatedData(
        @JsonProperty("id") String id,
        @JsonProperty("post_id") String postId,
        @JsonProperty("platform_post_id") String platformPostId,
        @JsonProperty("platform") Platform platform,
        @JsonProperty("external_id") String externalId,
        @JsonProperty("parent_external_id") String parentExternalId,
        @JsonProperty("body") String body,
        @JsonProperty("status") String status,
        @JsonProperty("author_external_id") String authorExternalId,
        @JsonProperty("author_name") String authorName,
        @JsonProperty("author_username") String authorUsername,
        @JsonProperty("author_avatar_url") String authorAvatarUrl,
        @JsonProperty("like_count") int likeCount,
        @JsonProperty("reply_count") int replyCount,
        @JsonProperty("is_hidden") boolean isHidden,
        @JsonProperty("permalink") String permalink,
        @JsonProperty("platform_data") Map<String, Object> platformData,
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("error") String error,
        @JsonProperty("error_details") ErrorDetails errorDetails,
        @JsonProperty("external_edited_at") String externalEditedAt,
        @JsonProperty("posted_at") String postedAt,
        @JsonProperty("created_at") String createdAt
) {}
