package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProfileGroup(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("profiles_count") int profilesCount
) {}
