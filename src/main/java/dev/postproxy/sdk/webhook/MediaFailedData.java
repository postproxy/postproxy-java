package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MediaFailedData(
        @JsonProperty("id") String id,
        @JsonProperty("post_id") String postId,
        @JsonProperty("content_type") String contentType,
        @JsonProperty("status") String status,
        @JsonProperty("error_message") String errorMessage
) {}
