package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DailyStatsResponse(
        @JsonProperty("data") DailyStats data
) {}
