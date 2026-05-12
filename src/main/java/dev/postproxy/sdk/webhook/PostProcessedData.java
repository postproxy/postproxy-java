package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.Platform;
import dev.postproxy.sdk.model.PostStatus;

import java.util.List;

public record PostProcessedData(
        @JsonProperty("id") String id,
        @JsonProperty("body") String body,
        @JsonProperty("status") PostStatus status,
        @JsonProperty("scheduled_at") String scheduledAt,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("platforms") List<Profile> platforms
) {
    public record Profile(
            @JsonProperty("id") String id,
            @JsonProperty("platform") Platform platform,
            @JsonProperty("name") String name
    ) {}
}
