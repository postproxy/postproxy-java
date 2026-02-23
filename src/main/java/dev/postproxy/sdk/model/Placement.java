package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Placement(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name
) {}
