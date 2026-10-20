package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.Platform;

import java.util.List;

/** Payload of {@code profile.placements_synced}: the profile fields plus its placements. */
public record PlacementsSyncedData(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("platform") Platform platform,
        @JsonProperty("profile_group_id") String profileGroupId,
        @JsonProperty("status") String status,
        @JsonProperty("uid") String uid,
        @JsonProperty("platform_id") String platformId,
        @JsonProperty("username") String username,
        @JsonProperty("placements_sync_status") String placementsSyncStatus,
        @JsonProperty("placements") List<PlacementEventData> placements
) {}
