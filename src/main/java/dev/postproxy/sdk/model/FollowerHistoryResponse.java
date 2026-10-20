package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FollowerHistoryResponse(
        @JsonProperty("data") FollowerHistory data
) {}
