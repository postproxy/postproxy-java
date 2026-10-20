package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.Platform;

public record ProfileEventData(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("platform") Platform platform,
        @JsonProperty("profile_group_id") String profileGroupId,
        @JsonProperty("status") String status,
        @JsonProperty("uid") String uid,
        @JsonProperty("platform_id") String platformId,
        @JsonProperty("username") String username,
        /** On {@code profile.disconnected}: {@code "user"} or {@code "automated"}. */
        @JsonProperty("origin") String origin
) {}
