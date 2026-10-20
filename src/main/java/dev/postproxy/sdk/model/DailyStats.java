package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record DailyStats(
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("platform") Platform platform,
        @JsonProperty("placement_id") String placementId,
        @JsonProperty("attribution") DailyStatsAttribution attribution,
        @JsonProperty("from") String from,
        @JsonProperty("to") String to,
        @JsonProperty("data_timezone") String dataTimezone,
        @JsonProperty("records") List<DailyStatsRecord> records,
        /** Describes each metric in {@code records}. */
        @JsonProperty("metrics") Map<String, Object> metrics
) {}
