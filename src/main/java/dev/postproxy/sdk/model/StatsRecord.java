package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record StatsRecord(
        @JsonProperty("stats") Map<String, Object> stats,
        @JsonProperty("recorded_at") String recordedAt
) {}
