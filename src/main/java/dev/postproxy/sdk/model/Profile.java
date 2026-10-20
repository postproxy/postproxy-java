package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Profile(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("username") String username,
        @JsonProperty("status") ProfileStatus status,
        /** Why an {@code INACTIVE} profile was deactivated, or null. */
        @JsonProperty("inactive_reason") ProfileInactiveReason inactiveReason,
        @JsonProperty("platform") Platform platform,
        /** The account's id on the platform. */
        @JsonProperty("platform_id") String platformId,
        @JsonProperty("profile_group_id") String profileGroupId,
        @JsonProperty("expires_at") String expiresAt,
        @JsonProperty("post_count") int postCount,
        @JsonProperty("avatar_url") String avatarUrl,
        @JsonProperty("platform_url") String platformUrl,
        @JsonProperty("placements_sync_status") String placementsSyncStatus
) {}
