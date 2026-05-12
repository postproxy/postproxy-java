package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.Platform;

import java.util.Map;

public record ProfileStatsData(
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("platform") Platform platform,
        @JsonProperty("placement_id") String placementId,
        @JsonProperty("stats") Map<String, Object> stats,
        @JsonProperty("recorded_at") String recordedAt
) {}
