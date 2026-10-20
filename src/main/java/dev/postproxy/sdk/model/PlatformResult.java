package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record PlatformResult(
        @JsonProperty("platform") Platform platform,
        @JsonProperty("profile_id") String profileId,
        @JsonProperty("profile_name") String profileName,
        @JsonProperty("status") PlatformPostStatus status,
        @JsonProperty("permalink") String permalink,
        @JsonProperty("params") Map<String, Object> params,
        @JsonProperty("error") String error,
        @JsonProperty("error_details") ErrorDetails errorDetails,
        @JsonProperty("attempted_at") String attemptedAt,
        @JsonProperty("insights") Insights insights,
        @JsonProperty("attachments") List<PlatformAttachment> attachments
) {}
