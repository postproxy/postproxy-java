package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.Platform;

public record PostImportedData(
        @JsonProperty("id") String id,
        @JsonProperty("body") String body,
        @JsonProperty("source") String source,
        @JsonProperty("posted_at") String postedAt,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("platform") Platform platform,
        @JsonProperty("profile") Profile profile,
        @JsonProperty("platform_post_id") String platformPostId,
        @JsonProperty("public_id") String publicId
) {
    public record Profile(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("platform") Platform platform
    ) {}
}
