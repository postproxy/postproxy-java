package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.ErrorDetails;
import dev.postproxy.sdk.model.Platform;
import dev.postproxy.sdk.model.PlatformPostStatus;

import java.util.Map;

public record PlatformPostData(
        @JsonProperty("id") String id,
        @JsonProperty("post_id") String postId,
        @JsonProperty("platform") Platform platform,
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("profile_name") String profileName,
        @JsonProperty("status") PlatformPostStatus status,
        @JsonProperty("error") String error,
        @JsonProperty("error_details") ErrorDetails errorDetails,
        @JsonProperty("platform_id") String platformId,
        @JsonProperty("insights") Map<String, Object> insights
) {}
