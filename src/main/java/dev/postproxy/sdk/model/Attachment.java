package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Attachment(
        @JsonProperty("id") String id,
        @JsonProperty("type") String type,
        @JsonProperty("url") String url,
        @JsonProperty("status") MediaStatus status,
        @JsonProperty("external_id") String externalId
) {}
