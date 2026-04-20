package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeletingPlatform(
        @JsonProperty("post_profile_id") String postProfileId,
        @JsonProperty("platform") Platform platform
) {}
