package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConnectionResponse(
        @JsonProperty("url") String url,
        @JsonProperty("success") boolean success
) {}
