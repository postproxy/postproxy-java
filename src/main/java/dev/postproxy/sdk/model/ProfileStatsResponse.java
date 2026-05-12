package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProfileStatsResponse(
        @JsonProperty("data") ProfileStats data
) {}
