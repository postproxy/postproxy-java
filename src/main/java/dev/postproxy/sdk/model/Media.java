package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record Media(
        @JsonProperty("id") String id,
        @JsonProperty("status") MediaStatus status,
        @JsonProperty("error_message") String errorMessage,
        @JsonProperty("content_type") String contentType,
        @JsonProperty("source_url") String sourceUrl,
        @JsonProperty("url") String url,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("platforms") List<MediaPlatformError> platforms
) {}
