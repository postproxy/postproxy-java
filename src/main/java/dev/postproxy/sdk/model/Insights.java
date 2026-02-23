package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Insights(
        @JsonProperty("impressions") Integer impressions,
        @JsonProperty("on") String on
) {}
