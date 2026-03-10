package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Media(
        @JsonProperty("id") String id,
        @JsonProperty("status") MediaStatus status,
        @JsonProperty("error_message") String errorMessage,
        @JsonProperty("content_type") String contentType,
        @JsonProperty("source_url") String sourceUrl,
        @JsonProperty("url") String url
) {}
