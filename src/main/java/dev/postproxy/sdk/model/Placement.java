package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record Placement(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("avatar_url") String avatarUrl,
        @JsonProperty("metadata") Map<String, Object> metadata
) {}
