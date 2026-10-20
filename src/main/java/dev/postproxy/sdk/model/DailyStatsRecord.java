package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One day of {@link DailyStats}. Normalized metrics (e.g. {@code impressions}, {@code viewers})
 * arrive as top-level keys and are collected into {@link #metrics()}.
 */
public final class DailyStatsRecord {

    @JsonProperty("date")
    private String date;

    @JsonProperty("platform_stats")
    private Map<String, Object> platformStats;

    private final Map<String, Object> metrics = new LinkedHashMap<>();

    public String date() {
        return date;
    }

    public Map<String, Object> platformStats() {
        return platformStats;
    }

    public Map<String, Object> metrics() {
        return Collections.unmodifiableMap(metrics);
    }

    /** A single normalized metric, or null when absent. */
    public Object metric(String name) {
        return metrics.get(name);
    }

    @JsonAnySetter
    void setMetric(String name, Object value) {
        metrics.put(name, value);
    }
}
