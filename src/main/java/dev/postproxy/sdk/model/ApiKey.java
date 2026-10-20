package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiKey(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("profile_group_id") String profileGroupId,
        @JsonProperty("sandbox") boolean sandbox,
        /** Only returned by {@code apiKeys().create(...)} — store it, it can't be read again. */
        @JsonProperty("token") String token,
        @JsonProperty("created_at") String createdAt
) {}
