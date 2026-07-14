package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Placement returned by {@code profiles().assignPlacementToGroup}, including
 * the profile group it now belongs to.
 */
public record AssignedPlacement(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("metadata") Map<String, Object> metadata,
        @JsonProperty("profile_group_id") String profileGroupId
) {}
