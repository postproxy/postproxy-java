package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record Post(
        @JsonProperty("id") String id,
        @JsonProperty("body") String body,
        @JsonProperty("status") PostStatus status,
        @JsonProperty("scheduled_at") String scheduledAt,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("draft") Boolean draft,
        /** {@code "postproxy"}, or {@code "imported"} for posts pulled in by a post sync. */
        @JsonProperty("source") String source,
        @JsonProperty("media") List<Media> media,
        @JsonProperty("platforms") List<PlatformResult> platforms,
        @JsonProperty("thread") List<ThreadChild> thread,
        @JsonProperty("queue_id") String queueId,
        @JsonProperty("queue_priority") String queuePriority
) {}
