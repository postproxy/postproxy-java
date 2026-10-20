package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FollowerHistory(
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("platform") Platform platform,
        @JsonProperty("placement_id") String placementId,
        @JsonProperty("from") String from,
        @JsonProperty("to") String to,
        @JsonProperty("records") List<FollowerHistoryRecord> records
) {}
