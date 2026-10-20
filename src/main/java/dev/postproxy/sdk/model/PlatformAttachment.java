package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** A file PostProxy generated for one platform, e.g. a TikTok cover. */
public record PlatformAttachment(
        @JsonProperty("id") String id,
        @JsonProperty("type") String type,
        @JsonProperty("status") String status,
        @JsonProperty("content_type") String contentType,
        @JsonProperty("url") String url
) {}
