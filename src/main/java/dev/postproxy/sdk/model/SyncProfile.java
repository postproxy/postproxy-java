package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SyncProfile(
        @JsonProperty("id") String id,
        @JsonProperty("network") Platform network,
        @JsonProperty("name") String name,
        @JsonProperty("external_username") String externalUsername
) {}
