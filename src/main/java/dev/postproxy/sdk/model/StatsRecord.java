package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * A stats snapshot. {@code rawStats} carries every metric under its original
 * platform name, e.g. {@code views} for Instagram or {@code impression_count}
 * for Twitter/X.
 */
public record StatsRecord(
        @JsonProperty("stats") Map<String, Object> stats,
        @JsonProperty("raw_stats") Map<String, Object> rawStats,
        @JsonProperty("recorded_at") String recordedAt
) {}
