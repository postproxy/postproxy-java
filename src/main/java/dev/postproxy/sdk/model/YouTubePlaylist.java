package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** A YouTube playlist. Publish into one with {@code YouTubeParams.playlistId}. */
public record YouTubePlaylist(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("privacy_status") YouTubePrivacy privacyStatus,
        @JsonProperty("item_count") Integer itemCount,
        @JsonProperty("thumbnail_url") String thumbnailUrl,
        @JsonProperty("published_at") String publishedAt
) {}
