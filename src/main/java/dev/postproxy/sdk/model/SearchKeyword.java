package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SearchKeyword(
        @JsonProperty("keyword") String keyword,
        // null for low-volume keywords; see impressionsThreshold.
        @JsonProperty("impressions") Long impressions,
        // Upper bound when Google reports no exact count.
        @JsonProperty("impressions_threshold") Long impressionsThreshold
) {}
