package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Account-level placement, returned by {@code placements().list()}. A placement (a Facebook Page,
 * LinkedIn organization, WhatsApp number, ...) can be reachable through several connected
 * profiles; one of them is active.
 */
public record AccountPlacement(
        @JsonProperty("id") String id,
        @JsonProperty("platform") Platform platform,
        @JsonProperty("external_id") String externalId,
        @JsonProperty("name") String name,
        @JsonProperty("avatar_url") String avatarUrl,
        @JsonProperty("status") String status,
        @JsonProperty("active_profile") AccountPlacementProfile activeProfile,
        @JsonProperty("linked_profiles") List<AccountPlacementProfile> linkedProfiles
) {}
