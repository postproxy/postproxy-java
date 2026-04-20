package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record PlatformResult(
        @JsonProperty("platform") Platform platform,
        @JsonProperty("status") PlatformPostStatus status,
        @JsonProperty("params") Map<String, Object> params,
        @JsonProperty("error") String error,
        @JsonProperty("error_details") ErrorDetails errorDetails,
        @JsonProperty("attempted_at") String attemptedAt,
        @JsonProperty("insights") Insights insights
) {}
