package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BlueskyConnectionResponse(
        @JsonProperty("success") boolean success,
        @JsonProperty("profile") SyncProfile profile
) {}
