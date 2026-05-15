package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MediaPlatformError(
        @JsonProperty("platform") Platform platform,
        @JsonProperty("status") PlatformPostStatus status,
        @JsonProperty("error") String error,
        @JsonProperty("error_details") ErrorDetails errorDetails
) {}
