package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** A connected profile a placement is reachable through. */
public record AccountPlacementProfile(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("connected") boolean connected
) {}
