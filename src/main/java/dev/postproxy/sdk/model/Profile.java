package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Profile(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("status") ProfileStatus status,
        @JsonProperty("platform") Platform platform,
        @JsonProperty("profile_group_id") String profileGroupId,
        @JsonProperty("expires_at") String expiresAt,
        @JsonProperty("post_count") int postCount
) {}
