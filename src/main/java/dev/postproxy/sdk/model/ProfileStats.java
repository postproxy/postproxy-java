package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ProfileStats(
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("platform") Platform platform,
        @JsonProperty("placement_id") String placementId,
        @JsonProperty("records") List<StatsRecord> records
) {}
